package com.uas.erp.service.plm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SaleDao;
import com.uas.erp.service.plm.SalePLMService;
import com.uas.erp.service.scm.SaleClashService;

@Service
public class SalePLMServiceImpl implements SalePLMService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private SaleClashService saleClashService;

	@Override
	public void deleteSale(int sa_id, String caller) {
		// 只能删除[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sa_id);
		// 删除sale
		baseDao.deleteById("sale", "sa_id", sa_id);
		// 删除saleDetail
		saleDao.deleteSale(sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sa_id);
	}

	@Override
	public void saveSale(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("sale", "sa_code='" + store.get("sa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.save_sacodeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		// 保存sale
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "Sale", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存saleDetail
		Object[] sd_id = new Object[grid.size()];
		StringBuffer error = new StringBuffer();
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			sd_id[i] = baseDao.getSeqId("SALEDETAIL_SEQ");
			map.put("sd_id", sd_id[i]);
			map.put("sd_statuscode", "ENTERING");
			map.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
			// 金额
			double total = Double.parseDouble(map.get("sd_qty").toString()) * Double.parseDouble(map.get("sd_price").toString());
			map.put("sd_total", NumberUtil.formatDouble(total, 3));
			// 不含税金额
			double price = Double.parseDouble(map.get("sd_price").toString())
					* (1 - Double.parseDouble(map.get("sd_taxrate").toString()) / 100);
			map.put("sd_costprice", price);
			total = Double.parseDouble(map.get("sd_qty").toString()) * price;
			map.put("sd_taxtotal", NumberUtil.formatDouble(total, 3));
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid, "SaleDetail");
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update sale set sa_total=(select sum(sd_total) from saledetail where saledetail.sd_said = sale.sa_id)");
		Object total = baseDao.getFieldDataByCondition("Sale", "sa_total", "sa_id=" + store.get("sa_id"));
		if (total != null) {
			baseDao.execute("update sale set sa_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE sa_id="
					+ store.get("sa_id"));
		}
		// 记录操作
		baseDao.logger.save(caller, "sa_id", store.get("sa_id"));
		// 执行保存后的其它逻辑
		handlerService.afterSave(caller, new Object[] { store, grid });
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void updateSale(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + store.get("sa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 设置更新时间
		store.put("sa_updatedate", DateUtil.currentDateString(null));
		// 更新sale
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "Sale", "sa_id");
		baseDao.execute(formSql);
		// 更新saleDetail
		StringBuffer error = new StringBuffer();
		for (Map<Object, Object> s : gstore) {
			// 金额
			double total = Double.parseDouble(s.get("sd_qty").toString()) * Double.parseDouble(s.get("sd_price").toString());
			s.put("sd_total", NumberUtil.formatDouble(total, 3));
			// 不含税金额
			double price = Double.parseDouble(s.get("sd_price").toString())
					* (1 - Double.parseDouble(s.get("sd_taxrate").toString()) / 100);
			s.put("sd_costprice", price);
			total = Double.parseDouble(s.get("sd_qty").toString()) * price;
			s.put("sd_taxtotal", NumberUtil.formatDouble(total, 3));
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore, "SaleDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			Object sdid = s.get("sd_id");
			s.put("sd_total", Float.parseFloat(s.get("sd_qty").toString()) * Double.parseDouble(s.get("sd_price").toString()));
			if (sdid == null || sdid.equals("") || sdid.equals("0") || Integer.parseInt(sdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SALEDETAIL_SEQ");
				s.put("sd_id", id);
				s.put("sd_statuscode", "ENTERING");
				s.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleDetail", new String[] { "sd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update sale set sa_total=(select sum(sd_total) from saledetail where saledetail.sd_said = sale.sa_id)");
		Object total = baseDao.getFieldDataByCondition("Sale", "sa_total", "sa_id=" + store.get("sa_id"));
		if (total != null) {
			baseDao.execute("update sale set sa_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE sa_id="
					+ store.get("sa_id"));
		}
		// 记录操作
		baseDao.logger.update(caller, "sa_id", store.get("sa_id"));
		// 执行修改后的其它逻辑
		handlerService.afterUpdate(caller, new Object[] { store, gstore });
		if (error.length() > 0) {
			BaseUtil.showError("AFTERSUCCESS" + error.toString());
		}
	}

	@Override
	public void auditSale(int sa_id, String caller) {
		baseDao.execute("update saledetail set sd_saleforecastdetailid=(select sd_id from SaleForecastDetail,SaleForecast where sd_sfid=sf_id and SaleForecast.sf_code=SaleDetail.sd_forecastcode and SaleDetail.sd_forecastdetno=SaleForecastDetail.sd_detno) where sd_said="
				+ sa_id + " and nvl(sd_forecastcode,' ')<>' '");
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sa_id);
		// 执行审核操作
		baseDao.audit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditman");
		baseDao.audit("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		saleClashService.createSaleClash(sa_id, "Sale");
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", sa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sa_id);
	}

	@Override
	public void resAuditSale(int sa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resAuditOnlyAudit(status);
		handlerService.beforeResAudit(caller, sa_id);
		// 反审核操作
		baseDao.resAudit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode", "sa_auditdate", "sa_auditman");
		baseDao.resOperate("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		saleClashService.cancelSaleClash(sa_id, "Sale");
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", sa_id);
		// 执行审核后的其它逻辑
		handlerService.afterResAudit(caller, sa_id);
	}

	// 是否符合冲销政策检测
	private void check_saleclash(Object sa_id) {
		Object dets;
		boolean uncheckbool = baseDao.isDBSetting("Sale", "ifNotClashCheck");
		/*
		 * if (baseDao.isDBSetting(caller, "ifNotClashCheck")) { return;//
		 * 不需要进行符合冲销政策检测 }
		 */
		Object[] obj = baseDao.getFieldsDataByCondition("sale,SaleKind", "sk_clashfor,sk_clashoption,sk_clashkind,sa_custcode,sa_sellercode",
				"(sa_kind=sk_name or sa_kind=sk_code) and sa_id=" + sa_id);
		if (obj != null) {
			if (obj[0] == null && !uncheckbool) {
				BaseUtil.showError("订单类型中的冲销匹配规则没有填写，不允许进行当前操作！");
			}
			if (obj[1] == null && !uncheckbool) {
				BaseUtil.showError("订单类型中的冲销触发类型没有填写，不允许进行当前操作！");
			}
			if (obj[2] == null && !uncheckbool) {
				BaseUtil.showError("订单类型中的附加冲销条件没有填写，不允许进行当前操作！");
			}
			if (uncheckbool) {
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(SD.sd_forecastcode,' ')<>' ' and FD.sd_id is null ",
								String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("预测单号填写错误，行号：" + dets);
				}
				dets = baseDao
						.getJdbcTemplate()
						.queryForObject(
								"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(SD.sd_forecastcode,' ')<>' ' and NVL(FD.sd_qty,0)<SD.sd_qty ",
								String.class, sa_id);
				if (dets != null) {
					BaseUtil.showError("订单数超出预测单剩余数！行号：" + dets);
				}
				if (obj[0] != null && obj[0].equals("单号冲销")) {
					if (obj[1] != null && obj[1].equals("订单冲销")) {
						dets = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select wmsys.wm_concat(sd_detno) from saledetail SD left join saleforecast F on F.sf_code=SD.sd_forecastcode left join saleforecastkind K on F.sf_kind=K.sf_name  where SD.sd_said=? and NVL(SD.sd_forecastcode,' ')<>' ' and NVL(K.sf_clashoption,' ') not in ('订单冲销','SALE') ",
										String.class, sa_id);
						if (dets != null) {
							BaseUtil.showError("填写的预测单号不是订单冲销类型的预测！行号：" + dets);
						}
					}
				}
			} else {
				if (obj[0].equals("单号冲销")) {
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wmsys.wm_concat(sd_detno) from saledetail where sd_said=? and NVL(sd_noforecast,0)=0 and (nvl(sd_forecastcode,' ')=' ' or nvl(sd_forecastdetno,0)=0) ",
									String.class, sa_id);
					if (dets != null) {
						BaseUtil.showError("明细行预测单号跟预测行号有未填写的，不允许进行当前操作！行号：" + dets);
					}
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and (NVL(SD.sd_noforecast,0)=0 or SD.sd_forecastcode<>' ')and FD.sd_id is null ",
									String.class, sa_id);
					if (dets != null) {
						BaseUtil.showError("预测单号填写错误，行号：" + dets);
					}
					dets = baseDao
							.getJdbcTemplate()
							.queryForObject(
									"select wmsys.wm_concat(SD.sd_detno) from saledetail SD left join sale on sd_said=sa_id left join saleforecast on sf_code=SD.sd_forecastcode left join saleforecastdetail FD on sf_id=sd_sfid and FD.sd_detno=SD.sd_forecastdetno where sa_id=? and NVL(sd_noforecast,0)=0 and NVL(FD.sd_qty,0)<SD.sd_qty ",
									String.class, sa_id);
					if (dets != null) {
						BaseUtil.showError("订单数超出预测单剩余数！行号：" + dets);
					}
					if (obj[1].equals("订单冲销")) {
						dets = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select wmsys.wm_concat(sd_detno) from saledetail SD left join saleforecast F on F.sf_code=SD.sd_forecastcode left join saleforecastkind K on F.sf_kind=K.sf_name  where SD.sd_said=? and NVL(SD.sd_noforecast,0)=0 and NVL(K.sf_clashoption,' ') not in ('订单冲销','SALE') ",
										String.class, sa_id);
						if (dets != null) {
							BaseUtil.showError("填写的预测单号不是订单冲销类型的预测！行号：" + dets);
						}
					}
				} else if (obj[0].equals("料号冲销")) {
					if (obj[1].equals("订单冲销") || obj[1].equals("SALE")) {
						String condi = "";
						if (obj[2].equals("客户匹配")) {
							condi = " and sf_custcode='" + obj[3] + "'";
						} else if(obj[2].equals("业务员")){
							condi = " and sf_sellercode='"+obj[4]+"' ";
						}
						dets = baseDao
								.getJdbcTemplate()
								.queryForObject(
										"select  wmsys.wm_concat('物料:'||A.sd_prodcode||'剩余预测数:'||B.qty) from (select sd_prodcode,sum(sd_qty) qty from saledetail where sd_said=? and NVL(sd_noforecast,0)=0 group by sd_prodcode)A left join (select sd_prodcode,NVL(sum(sd_qty),0)qty from saleforecast,saleforecastdetail,saleforecastkind where saleforecast.sf_id=sd_sfid and saleforecast.sf_kind=saleforecastkind.sf_name and saleforecastkind.sf_clashoption in ('订单冲销','SALE') and saleforecast.sf_statuscode='AUDITED' and sd_qty>0 and trunc(sd_enddate)>=trunc(sysdate) and NVL(sd_statuscode,' ')<>'FINISH' "
												+ condi + " group by sd_prodcode)B on A.sd_prodcode=B.sd_prodcode where A.qty>NVL(B.qty,0)",
										String.class, sa_id);
						if (dets != null) {
							BaseUtil.showError("订单数超出预测单剩余数！" + dets);
						}
					}
				}
			}
		}
	}

	@Override
	public void submitSale(int sa_id, String caller) {
		baseDao.execute("update saledetail set sd_saleforecastdetailid=(select sd_id from SaleForecastDetail,SaleForecast where sd_sfid=sf_id and SaleForecast.sf_code=SaleDetail.sd_forecastcode and SaleDetail.sd_forecastdetno=SaleForecastDetail.sd_detno) where sd_said="
				+ sa_id + " and nvl(sd_forecastcode,' ')<>' '");
		// 只能提交状态为[在录入]的合同!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.submitOnlyEntering(status);
		// 判断订单明细是否填写数量
		boolean bool = baseDao.checkByCondition("SaleDetail", "sd_said=" + sa_id + " AND (sd_qty is null OR sd_qty=0)");
		if (bool) {
			// 执行提交前的其它逻辑
			handlerService.beforeSubmit(caller, sa_id);
			// 符合冲销政策检测
			check_saleclash(sa_id);
			// 执行提交操作
			baseDao.submit("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
			baseDao.submit("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
			// 记录操作
			baseDao.logger.submit(caller, "sa_id", sa_id);
			// 修改主单据的总金额
			baseDao.execute("update sale set sa_total=(select sum(sd_total) from saledetail where saledetail.sd_said = sale.sa_id)");
			Object total = baseDao.getFieldDataByCondition("Sale", "sa_total", "sa_id=" + sa_id);
			if (total != null) {
				baseDao.execute("update sale set sa_totalupper='" + MoneyUtil.toChinese(total.toString()) + "' WHERE sa_id=" + sa_id);
			}
			// 执行提交后的其它逻辑
			handlerService.afterSubmit(caller, sa_id);
		} else {
			BaseUtil.showError("存在未填写数量的订单明细!");
		}
	}

	@Override
	public void resSubmitSale(int sa_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sa_id);
		// 执行反提交操作
		baseDao.resOperate("Sale", "sa_id=" + sa_id, "sa_status", "sa_statuscode");
		baseDao.resOperate("SaleDetail", "sd_said=" + sa_id, "sd_status", "sd_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sa_id", sa_id);
		handlerService.afterResSubmit(caller, sa_id);

	}

	@Override
	public void printSale(int sa_id, String caller) {
		// 判断已审核才允许打印
		if (baseDao.isDBSetting(caller, "printNeedAudit")) {
			String status = baseDao.getFieldValue("Sale", "sa_statuscode", "sa_id=" + sa_id, String.class);
			StateAssert.printOnlyAudited(status);
		}
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sa_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print(caller, "sa_id", sa_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sa_id });
	}

	@Override
	public void endSale(int id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + id);
		StateAssert.end_onlyAudited(status); // 结案
		baseDao.updateByCondition("Sale", "sa_statuscode='FINISH',sa_status='" + BaseUtil.getLocalMessage("FINISH")
				+ "',sa_enddate=sysdate", "sa_id=" + id);
		baseDao.updateByCondition("SaleDetail", "SD_MRPSTATUSCODE='FINISH',SD_MRPSTATUS='" + BaseUtil.getLocalMessage("FINISH")
				+ "',sd_statuscode='FINISH',sd_status='" + BaseUtil.getLocalMessage("FINISH") + "',sd_enddate=sysdate", "sd_said=" + id);
		// 记录操作
		baseDao.logger.end(caller, "sa_id", id);
	}

	@Override
	public void resEndSale(int id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("Sale", "sa_statuscode", "sa_id=" + id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition("Sale",
				"sa_statuscode='AUDITED',sa_status='" + BaseUtil.getLocalMessage("AUDITED") + "',sa_enddate=null", "sa_id=" + id);
		baseDao.updateByCondition("SaleDetail",
				"SD_MRPSTATUSCODE=null,SD_MRPSTATUS=null,sd_statuscode='AUDITED',sd_status='" + BaseUtil.getLocalMessage("AUDITED")
						+ "',sd_enddate=null", "sd_said=" + id);
		// 记录操作
		baseDao.logger.resEnd(caller, "sa_id", id);
	}

	@Override
	public int turnSendNotify(int sa_id, String caller) {
		int snid = 0;
		// 判断该销售单是否已经转入过转发货通知单
		Object code = baseDao.getFieldDataByCondition("SaleDetail", "sd_code", "sd_said=" + sa_id);
		code = baseDao.getFieldDataByCondition("sendNotifyDetail", "snd_code", "snd_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("scm.sale.sale.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/scm/sale/sendNotify.jsp?formCondition=sn_codeIS" + code
					+ "&gridCondition=snd_codeIS" + code + "')\">" + code + "</a>&nbsp;");
		} else {
			// 转发货通知单
			snid = saleDao.turnSendNotify(sa_id);
			// 修改销售单状态
			baseDao.updateByCondition("Sale", "sa_statuscode='TURNSA',sa_status='" + BaseUtil.getLocalMessage("TURNSA") + "'", "sa_id="
					+ sa_id);
			baseDao.updateByCondition("SaleDetail", "sd_yqty=sd_yqty", "sd_said=" + sa_id);
			// 记录操作
			baseDao.logger.turn("msg.turnSendNotify", caller, "sa_id", sa_id);
		}
		return snid;
	}

}

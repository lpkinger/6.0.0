package com.uas.erp.service.scm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.SaleClashService;

@Service("saleClashService")
public class SaleClashServiceImpl implements SaleClashService {
	@Autowired
	private BaseDao baseDao;

	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveSaleClash(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("SaleClash", "sc_code='" + store.get("sc_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, grid });
		// 保存SaleClash
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleClash", new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// //保存SaleClashDetail
		List<String> gridSql = SqlUtil.getInsertSqlbyList(grid, "SaleClashDetail", "scd_id");
		baseDao.execute(gridSql);
		baseDao.logger.save(caller, "sc_id", store.get("sc_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, grid });
	}

	@Override
	public void deleteSaleClash(int sc_id, String caller) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { sc_id });
		// 删除SaleClash
		baseDao.deleteById("SaleClash", "sc_id", sc_id);
		// 删除SaleClashDetail
		baseDao.deleteById("SaleClashdetail", "scd_scid", sc_id);
		// 记录操作
		baseDao.logger.delete(caller, "sc_id", sc_id);
		// 冲销数量记录在预测单中
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select scd_ordercode,scd_orderdetno,sum(scd_clashqty) scd_clashqty from SaleClashDetail where scd_scid=? group by scd_ordercode,scd_orderdetno",
						sc_id);
		while (rs.next()) {
			baseDao.updateByCondition(
					"SaleForecastDetail",
					"sd_clashsaleqty=nvl(sd_clashsaleqty,0)-" + rs.getDouble("scd_clashqty"),
					"sd_detno=" + rs.getInt("scd_orderdetno") + " AND sd_sfid=(SELECT sf_id FROM SaleForecast WHERE sf_code='"
							+ rs.getString("scd_ordercode") + "')");
		}
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { sc_id });
	}

	@Override
	public void updateSaleClashById(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + store.get("sc_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.handler(caller, "save", "before", new Object[] { store, gstore });
		// 修改SaleClash
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleClash", "sc_id");
		baseDao.execute(formSql);
		// 修改SaleClashDetail
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gridStore, "SaleClashDetail", "scd_id");
		for (Map<Object, Object> s : gstore) {
			if (s.get("scd_id") == null || s.get("scd_id").equals("") || s.get("scd_id").equals("0")
					|| Integer.parseInt(s.get("scd_id").toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SALECLASHDETAIL_SEQ");
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleClashDetail", new String[] { "scd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 记录操作
		baseDao.logger.update(caller, "sc_id", store.get("sc_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store, gstore });
	}

	@Override
	public void printSaleClash(int sc_id, String caller) {
		// 执行打印前的其它逻辑
		handlerService.handler(caller, "print", "before", new Object[] { sc_id });
		// 执行打印操作
		// TODO
		// 记录操作
		baseDao.logger.print(caller, "sc_id", sc_id);
		// 执行打印后的其它逻辑
		handlerService.handler(caller, "print", "after", new Object[] { sc_id });
	}

	@Override
	public void auditSaleClash(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.auditOnlyCommited(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('预测单号:'||sf_code||',行号:'||sd_detno) from (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where  (nvl(sd_statuscode,' ')<>'AUDITED' and not(sd_statuscode='COMMITED' and sf_statuscode='AUDITED')) and (sf_code,sd_detno) in (select scd_ordercode,scd_orderdetno from SaleClashDetail where scd_scid=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号+预测行号状态不等于已审核，不允许已审核!" + dets);
		}
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { sc_id });
		// 执行审核操作
		baseDao.audit("SaleClash", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		Object source = baseDao.getFieldDataByCondition("SaleClash", "sc_source", "sc_id=" + sc_id);
		// 冲销数量记录在预测单中
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select scd_ordercode,scd_orderdetno,sum(scd_clashqty) scd_clashqty from SaleClashDetail where scd_scid=? group by scd_ordercode,scd_orderdetno",
						sc_id);
		while (rs.next()) {
			baseDao.updateByCondition("SaleForecastDetail", "sd_qty=sd_qty-(" + rs.getDouble("scd_clashqty")
					+ "),sd_clashsaleqty=nvl(sd_clashsaleqty,0)+" + rs.getDouble("scd_clashqty"), "sd_detno=" + rs.getInt("scd_orderdetno")
					+ " AND sd_sfid=(SELECT sf_id FROM SaleForecast WHERE sf_code='" + rs.getString("scd_ordercode") + "')");
			if (source != null && source.equals("销售单") && rs.getGeneralDouble("scd_clashqty") > 0) {
				baseDao.updateByCondition("SaleForecastDetail", "sd_yqty=(case when NVL(sd_yqty,0)-(" + rs.getDouble("scd_clashqty")
						+ ")<0 then 0 else sd_yqty-(" + rs.getDouble("scd_clashqty") + ") end)", "sd_detno=" + rs.getInt("scd_orderdetno")
						+ " AND sd_sfid=(SELECT sf_id FROM SaleForecast WHERE sf_code='" + rs.getString("scd_ordercode") + "')");
			}
		}
		// 记录操作
		baseDao.logger.audit(caller, "sc_id", sc_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { sc_id });
	}

	@Override
	public void resAuditSaleClash(int sc_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resAuditOnlyAudit(status);
		// 执行反审核操作
		baseDao.resAudit("SaleClash", "sc_id=" + sc_id, "sc_status", "sc_statuscode", "sc_auditdate", "sc_auditman");
		Object source = baseDao.getFieldDataByCondition("SaleClash", "sc_source", "sc_id=" + sc_id);
		// 冲销数量记录在预测单中
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select scd_ordercode,scd_orderdetno,sum(scd_clashqty) scd_clashqty from SaleClashDetail where scd_scid=? group by scd_ordercode,scd_orderdetno",
						sc_id);
		while (rs.next()) {
			baseDao.updateByCondition(
					"SaleForecastDetail",
					"sd_qty=sd_qty+" + rs.getDouble("scd_clashqty") + ",sd_clashsaleqty=nvl(sd_clashsaleqty,0)-("
							+ rs.getDouble("scd_clashqty") + ")",
					"sd_detno=" + rs.getInt("scd_orderdetno") + " AND sd_sfid=(SELECT sf_id FROM SaleForecast WHERE sf_code='"
							+ rs.getString("scd_ordercode") + "')");
			if (source != null && source.equals("销售单")) {
				baseDao.updateByCondition(
						"SaleForecastDetail",
						"sd_yqty=sd_yqty+" + rs.getDouble("scd_clashqty"),
						"sd_detno=" + rs.getInt("scd_orderdetno") + " AND sd_sfid=(SELECT sf_id FROM SaleForecast WHERE sf_code='"
								+ rs.getString("scd_ordercode") + "')");
			}
		}
		// 记录操作
		baseDao.logger.resAudit(caller, "sc_id", sc_id);
	}

	@Override
	public void submitSaleClash(int sc_id, String caller) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.submitOnlyEntering(status);
		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('预测单号:'||sf_code||',行号:'||sd_detno) from (select sf_code,sd_detno from SaleForecast left join SaleForecastDetail on sf_id=sd_sfid where nvl(sd_statuscode,' ')<>'AUDITED' and (sf_code,sd_detno) in (select scd_ordercode,scd_orderdetno from SaleClashDetail where scd_scid=?))",
						String.class, sc_id);
		if (dets != null) {
			BaseUtil.showError("预测单号+预测行号状态不等于已审核，不允许提交!" + dets);
		}
		// 冲销数量是否大于预测数量
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select scd_ordercode,scd_orderdetno,sum(scd_clashqty) scd_clashqty from SaleClashDetail where scd_scid=? group by scd_ordercode,scd_orderdetno",
						sc_id);
		while (rs.next()) {
			Object obj = baseDao.getFieldDataByCondition(
					"SaleForecastDetail left join SaleForecast on sf_id=sd_sfid",
					"sd_qty",
					"sf_code='" + rs.getString("scd_ordercode") + "' and sd_detno=" + rs.getInt("scd_orderdetno") + " and sd_qty<"
							+ rs.getDouble("scd_clashqty"));
			if (obj != null) {
				BaseUtil.showError("预测单号[" + rs.getString("scd_ordercode") + "],预测单行号[" + rs.getInt("scd_orderdetno") + "]的冲销数量大于剩余可冲销数量["
						+ obj + "],不能提交！");
			}
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { sc_id });
		// 执行提交操作
		baseDao.submit("SaleClash", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "sc_id", sc_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { sc_id });
	}

	@Override
	public void resSubmitSaleClash(int sc_id, String caller) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("SaleClash", "sc_statuscode", "sc_id=" + sc_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { sc_id });
		// 执行反提交操作
		baseDao.resOperate("SaleClash", "sc_id=" + sc_id, "sc_status", "sc_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "sc_id", sc_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { sc_id });
	}

	/**
	 * 产生冲销单并审核 发货单过帐/销售单审核 scm->prodinout->post->after scm->sale->audit->after
	 * 
	 * @author ZhongYL
	 */
	@Transactional
	public void createSaleClash(Integer fromid, String fromcaller) {
		Employee employee = SystemSession.getUser();
		String SqlStr = "", clashcode = "", canclash = "", othercondition = "";
		double thisqty = 0, needclashqty = 0, clashedqty = 0;
		int csid = 0, detno = 0;
		String prodcode = "", ordercode = "", fromcode = "", fromwhere = "";
		String clashoption = "", saleclashkind = "";
		SqlRowList rs0;
		if (fromcaller.equalsIgnoreCase("ProdInOut")) {
			fromwhere = baseDao.getFieldDataByCondition("ProdInOut", "pi_class", "pi_id=" + fromid).toString();
		} else if (fromcaller.equalsIgnoreCase("Sale") || fromcaller.equals("销售单")) {
			fromwhere = "销售单";
		} else {
			return;
		}
		// 先更新呆冲销的明细已冲销数为0
		SqlStr = "update saleforecastDetail set sd_stepqty=0 where sd_id in (select sd_id from saleforecastDetail ,saleforecast   where sd_sfid=sf_id and sf_statuscode='AUDITED' and NVL(sd_statuscode,' ')<>'FINISH' and sd_qty>0)  ";
		baseDao.execute(SqlStr);
		// 获取冲销来源单据
		if (fromwhere.equals("出货单")) {
			SqlStr = "select pd_id as sourcedetid,pd_inoutno as fromcode,pd_outqty+nvl(pd_beipinoutqty,0) as qty,pd_prodcode as prodcode,pd_ordercode as ordercode,pd_pdno as pdno,sd_forecastcode as sfcode,sd_forecastdetno as sfdetno from Prodiodetail left join saledetail on sd_code=pd_ordercode and sd_detno=pd_orderdetno where pd_piid='"
					+ fromid + "' ";
		} else if (fromwhere.equals("销售单")) {
			SqlStr = "select sd_id as sourcedetid,sd_code as fromcode,sd_qty as qty,sd_prodcode as prodcode,sa_code as ordercode,sd_detno as pdno,sd_forecastcode as sfcode,sd_forecastdetno as sfdetno,sa_custcode,sa_sellercode from saledetail,sale where sa_id=sd_said and sd_said='"
					+ fromid + "' ";
		} else if (fromwhere.equals("完工入库单") || fromwhere.equals("委外验收单") || fromwhere.equals("委外验退单")) {
			SqlStr = "select pd_id as sourcedetid,pd_inoutno as fromcode,NVL(pd_inqty,0)+NVL(pd_outqty,0) as qty,pd_prodcode as prodcode,pd_ordercode as ordercode,pd_pdno as pdno,ma_salecode,ma_saledetno from Prodiodetail,warehouse,make,makekind where pd_piid='"
					+ fromid
					+ "' and pd_whcode=wh_code and NVL(wh_ifclash,0)<>0  and ma_code=pd_ordercode and ma_kind=mk_name and NVL(mk_clashsale,0)<>0 ";
			// 判断是否存在需要完工入库冲销的预测
			rs0 = baseDao.queryForRowSet("select sf_code from saleforecastkind where sf_clashoption='完工冲销' or sf_clashoption='FINISH' ");
			if (!rs0.hasNext()) {
				// 不需要完工冲销
				return;
			}
		} else if (fromwhere.equals("销售拨出单")) {
			SqlStr = "select pd_id as sourcedetid,pd_inoutno as fromcode,pd_outqty+nvl(pd_beipinoutqty,0) as qty,pd_prodcode as prodcode,pd_plancode as ordercode,pd_pdno as pdno,pd_plancode as sfcode,pd_forecastdetno as sfdetno from Prodiodetail where  pd_piid='"
					+ fromid + "' and  NVL(pd_plancode,' ')<>' ' and pd_forecastdetno>0 ";
		} else if (fromwhere.equals("其它出库单")) {
			SqlStr = "select pd_id as sourcedetid,pd_inoutno as fromcode,pd_outqty+nvl(pd_beipinoutqty,0) as qty,pd_prodcode as prodcode,pd_plancode as ordercode,pd_pdno as pdno,pd_plancode as sfcode,pd_forecastdetno as sfdetno from Prodiodetail where  pd_piid='"
					+ fromid + "' and  NVL(pd_plancode,' ')<>' ' and pd_forecastdetno>0 ";
		} else {
			return;
		}
		detno = 1;
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		while (rs.next()) {
			fromcode = rs.getString("fromcode");
			clashoption = "";
			saleclashkind = "";
			othercondition = "";
			clashedqty = 0;
			needclashqty = rs.getDouble("qty");
			prodcode = rs.getString("prodcode");
			ordercode = rs.getString("ordercode");
			// 判断此行记录是否需要冲销
			canclash = "N";
			if (fromwhere.equals("销售单") || fromwhere.equals("出货单")) {
				if(fromwhere.equals("出货单") && baseDao.isDBSetting("ProdInOut!Sale", "clashSaleForecastByProduct")){
					saleclashkind="PRODUCT";
					clashoption = "SEND";
				}else{
					SqlStr = "SELECT * from sale left join salekind on (sa_kind=sk_name or sa_kind=sk_code) where sa_code='" + ordercode + "' ";
					SqlRowList rs2 = baseDao.queryForRowSet(SqlStr);
					if (rs2.next()) {
						saleclashkind = rs2.getString("sk_clashfor");
						clashoption = rs2.getString("sk_clashoption");
						othercondition = rs2.getString("sk_clashkind");
					} else {
						// 未设置类型，不冲销
						continue;
					}
				}
			} else if (fromwhere.equals("完工入库单") || fromwhere.equals("委外验收单") || fromwhere.equals("委外验退单")) {
				saleclashkind = "PRODUCT";
				clashoption = "FINISH";
			} else if (fromwhere.equals("销售拨出单") || fromwhere.equals("其它出库单")) {
				saleclashkind = "单号冲销";
				clashoption = "SEND";
			}
			if (saleclashkind == null || clashoption == null) {
				// 未设置类型，不冲销
				continue;
			}
			if (fromwhere.equals("销售单")) {
				if (clashoption.equals("订单冲销") || clashoption.equalsIgnoreCase("SALE")) {
					canclash = "Y";
				}
			} else if (fromwhere.equals("出货单")) {
				if (clashoption.equals("发货冲销") || clashoption.equalsIgnoreCase("SEND")) {
					canclash = "Y";
				}
			}
			if (fromwhere.equals("完工入库单") || fromwhere.equals("委外验收单") || fromwhere.equals("委外验退单") || fromwhere.equals("销售拨出单")
					|| fromwhere.equals("其它出库单")) {
				canclash = "Y";
			}
			if (canclash.equals("N")) {
				// 不冲销
				continue;
			}
			SqlStr = "";
			if (saleclashkind.equalsIgnoreCase("sale") || saleclashkind.equals("单号冲销")) {
				SqlStr = "SELECT saleforecastDetail.*,saleforecast.* from saleforecastDetail left join saleforecast on sd_sfid=sf_id left join saleforecastkind on (saleforecast.sf_kind=saleforecastkind.sf_name or saleforecast.sf_kind=saleforecastkind.sf_code) where saleforecast.sf_code='"
						+ rs.getString("sfcode") + "' and sd_detno=" + rs.getInt("sfdetno");
			} else if (saleclashkind.equalsIgnoreCase("product") || saleclashkind.equals("料号冲销")) {
				// 按物料号冲销
				SqlStr = "SELECT saleforecastDetail.*,saleforecast.* from saleforecastDetail left join saleforecast on sd_sfid=sf_id left join saleforecastkind on (saleforecast.sf_kind=saleforecastkind.sf_name or saleforecast.sf_kind=saleforecastkind.sf_code) where sd_prodcode='"
						+ prodcode + "' ";
				if (fromwhere.equals("完工入库单") || fromwhere.equals("委外验收单")) {
					SqlStr = "SELECT case when saleforecast.sf_code='"
							+ rs.getString("ma_salecode")
							+ "' and sd_detno="
							+ rs.getInt("ma_saledetno")
							+ " then 1 else 2 end sortid,saleforecastDetail.*,saleforecast.* from saleforecastDetail left join saleforecast on sd_sfid=sf_id left join saleforecastkind on (saleforecast.sf_kind=saleforecastkind.sf_name or saleforecast.sf_kind=saleforecastkind.sf_code) where sd_prodcode='"
							+ prodcode + "' ";
				}
				if (fromwhere.equals("销售单") && othercondition != null && othercondition.equals("客户匹配")) {
					SqlStr = SqlStr + " and (case when NVL(saleforecastDetail.sd_custcode,' ')<>' ' then saleforecastDetail.sd_custcode else sf_custcode end)='" + rs.getString("sa_custcode") + "' ";
				} else if (fromwhere.equals("销售单") && othercondition != null && othercondition.equals("业务员")){
					SqlStr = SqlStr + " and (case when nvl(saleforecastDetail.sd_sellercode,' ')<>' ' then saleforecastDetail.sd_sellercode else sf_sellercode end)='"+ rs.getString("sa_sellercode")+"' ";
				}
			} else {
				// 不冲销
				continue;
			}
			if (clashoption.equals("订单冲销") || clashoption.equalsIgnoreCase("SALE")) {
				SqlStr = SqlStr + " and sf_clashoption in ('SALE','订单冲销') ";
			} else if (clashoption.equals("发货冲销") || clashoption.equalsIgnoreCase("SEND")) {
				SqlStr = SqlStr + " and sf_clashoption in ('SEND','发货冲销') ";
			} else if (clashoption.equals("完工冲销") || clashoption.equalsIgnoreCase("FINISH")) {
				SqlStr = SqlStr + " and sf_clashoption in ('FINISH','完工冲销') ";
			}
			if (saleclashkind.equalsIgnoreCase("product") || saleclashkind.equals("料号冲销")) {
				SqlStr = SqlStr + " and trunc(sd_enddate)>=trunc(sysdate) ";// 只冲销有效的预测，@update 20170224 截止日期等于今日的也可冲销，与存储过程中的计算一致
			}
			if (fromwhere.equals("委外验退单")) {
				SqlStr = " SELECT saleforecastDetail.*,saleforecast.*,scd_id,NVL(scd_clashqty,0)scd_clashqty,NVL(scd_cancelqty,0)scd_cancelqty,scd_id from saleclashdetail left join saleclash on scd_scid=sc_id left join saleforecast on scd_ordercode=sf_code left join saleforecastdetail on sd_sfid=sf_id  where sc_source='委外验收单' and scd_fromcode='"
						+ rs.getString("ordercode")
						+ "' and scd_clashqty>0 and scd_clashqty-NVL(scd_cancelqty,0)>0 and sc_statuscode='AUDITED' and sd_clashsaleqty>0 ";
			}
			if (fromwhere.equals("完工入库单") || fromwhere.equals("委外验收单")) {
				if (baseDao.isDBSetting("SaleForecast", "mappingSaleForecast")) {
					SqlStr = SqlStr
							+ " and saleforecast.sf_code='"
							+ rs.getString("ma_salecode")
							+ "' and saleforecastdetail.sd_detno="
							+ rs.getInt("ma_saledetno")
							+ " and saleforecast.sf_statuscode='AUDITED' and sd_qty-nvl(sd_stepqty,0)>0 order by sortid,sd_needdate asc,sd_detno asc";
				} else {
					SqlStr = SqlStr
							+ " and saleforecast.sf_statuscode='AUDITED' and sd_qty-nvl(sd_stepqty,0)>0 order by sortid,sd_needdate asc,sd_detno asc";
				}
			} else if (fromwhere.equals("委外验退单")) {
				// 委外验退条件特殊，上面已加
			} else {
				SqlStr = SqlStr
						+ " and saleforecast.sf_statuscode='AUDITED' and NVL(sd_statuscode,' ')<>'FINISH' and sd_qty-nvl(sd_stepqty,0)>0 order by sd_needdate asc,sd_detno asc";
			}

			if ("Y".equals(canclash) && !SqlStr.equals("")) {
				SqlRowList rs2 = baseDao.queryForRowSet(SqlStr);
				while (rs2.next() && clashedqty < needclashqty) {
					double remain = rs2.getDouble("sd_qty") - rs2.getDouble("sd_stepqty");
					int cancelsourceid = 0;
					if (fromwhere.equals("委外验退单")) {
						remain = (rs2.getDouble("scd_clashqty") - rs2.getDouble("scd_cancelqty"));// 拿冲销数反冲减预测
						cancelsourceid = rs2.getInt("scd_id");
						if (remain <= 0) {
							continue;
						}
					}
					thisqty = remain > needclashqty - clashedqty ? needclashqty - clashedqty : remain;
					if (clashcode == "") {
						clashcode = baseDao.sGetMaxNumber("SaleClash", 2);
						csid = baseDao.getSeqId("SALECLASH_SEQ");
						SqlStr = "insert into SaleClash(sc_id,sc_code,sc_date,sc_status,sc_statuscode,sc_recorder,sc_source,sc_sourceid,sc_sourcecode)values("
								+ csid
								+ ",'"
								+ clashcode
								+ "',sysdate,'"
								+ BaseUtil.getLocalMessage("COMMITED")
								+ "','COMMITED','"
								+ employee.getEm_name() + "','" + fromwhere + "'," + fromid + ",'" + fromcode + "')   ";
						baseDao.execute(SqlStr);
					}
					SqlStr = "insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno,scd_sourcedetid,scd_fromcode,scd_cancelid)values("
							+ "SALECLASHDETAIL_SEQ.NEXTVAL,"
							+ csid
							+ ",'"
							+ detno
							+ "','"
							+ prodcode
							+ "','"
							+ thisqty
							+ "','"
							+ rs2.getString("sf_code")
							+ "','"
							+ rs2.getInt("sd_detno")
							+ "',"
							+ rs.getInt("sourcedetid")
							+ ",'"
							+ rs.getString("ordercode") + "'," + cancelsourceid + ")";
					baseDao.execute(SqlStr);
					if (fromwhere.equals("委外验退单")) {
						SqlStr = "UPDATE SaleClashDetail set scd_cancelqty=nvl(scd_cancelqty,0)+" + thisqty + " where scd_id="
								+ rs2.getInt("scd_id") + " ";
						baseDao.execute(SqlStr);
					}
					SqlStr = "UPDATE saleforecastDetail set sd_stepqty=nvl(sd_stepqty,0)+(" + thisqty + ") where sd_id="
							+ rs2.getInt("sd_id") + " ";
					baseDao.execute(SqlStr);
					detno = detno + 1;
					clashedqty = clashedqty + thisqty;
				}
			}
		}
		if (fromwhere.equals("委外验退单") && csid > 0) {
			SqlStr = "UPDATE SaleClashDetail set scd_clashqty=0-scd_clashqty  where scd_scid=" + csid + " ";
			baseDao.execute(SqlStr);
		}
		if (!"".equals(clashcode)) {
			// 审核冲销单
			auditSaleClash(csid, "SaleClash");
		}
	}

	/**
	 * 取消产生的冲销单 发货单反过帐/销售单反审核 scm->prodinout->resPost->after
	 * scm->sale->Unaudit->after
	 * 
	 * @author ZhongYL
	 */
	public void cancelSaleClash(Integer fromid, String fromcaller) {
		String SqlStr = "";
		String fromwhere = "";
		if (fromcaller.equalsIgnoreCase("ProdInOut")) {
			fromwhere = baseDao.getFieldDataByCondition("ProdInOut", "pi_class", "pi_id=" + fromid).toString();
			SqlStr = "select sc_code,sc_id,sc_statuscode from saleclash where  sc_sourceid='" + fromid + "' and sc_source='" + fromwhere
					+ "' ";
		} else if (fromcaller.equalsIgnoreCase("Sale")) {
			fromwhere = "销售单";
			SqlStr = "select sc_code,sc_id,sc_statuscode from saleclash where  sc_sourceid='" + fromid + "' and sc_source='" + fromwhere
					+ "' ";
		} else if (fromcaller.equalsIgnoreCase("SaleForecast")) {
			fromwhere = "销售预测单";
			SqlStr = "select sc_code,sc_id,sc_statuscode from saleclash where  sc_sourceid='" + fromid + "' and sc_source='" + fromwhere
					+ "' ";
		} else {
			return;
		}
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		if (rs.next()) {
			if (rs.getString("sc_statuscode").equals("AUDITED")) {
				// 反审核冲销单
				resAuditSaleClash(rs.getInt("sc_id"), "SaleClash");
				if (fromwhere.equals("委外验退单")) {
					baseDao.execute("UPDATE SaleClashDetail set scd_cancelqty=scd_cancelqty-(select sum(-A.scd_clashqty) from saleclashdetail A where A.scd_scid="
							+ rs.getInt("sc_id")
							+ " and A.scd_cancelid=saleclashdetail.scd_id ) where scd_id in (select scd_cancelid from saleclashdetail where scd_scid="
							+ rs.getInt("sc_id") + " )");
				}
			}
			// 删除冲销单
			deleteSaleClash(rs.getInt("sc_id"), "SaleClash");
		}
	}

	/**
	 * 如冲销的预测单号、序号的明细状态为已结案，限制反过账并提示 scm->prodinout->resPost->before
	 * 
	 */
	public void getSaleClash(Integer fromid, String fromcaller) {
		SqlRowList rs;
		String SqlStr = "";
		String fromwhere = "";
		fromwhere = baseDao.getFieldDataByCondition("ProdInOut", "pi_class", "pi_id=" + fromid).toString();
		SqlStr = "select sc_code,sc_id,sc_statuscode from saleclash where  sc_sourceid='" + fromid + "' and sc_source='" + fromwhere + "' ";
		rs = baseDao.queryForRowSet(SqlStr);
		if (rs.next()) {
			SqlStr = "select count(0) c,wm_concat('预测单号'|| sf_code ||'序号'||sd_detno ) dt  from saleclashdetail left join saleforecast on scd_ordercode=sf_code left join saleforecastdetail on sd_sfid=sf_id and sd_detno=scd_orderdetno"
					+ " where scd_scid='"
					+ rs.getString("sc_id")
					+ "' and ( sf_statuscode='AUDITED' and NVL(sd_statuscode,' ')<>'FINISH') " + " and sd_id is null ";
			rs = baseDao.queryForRowSet(SqlStr);
			if (rs.next()) {
				if (rs.getInt("c") > 0) {
					BaseUtil.showError("冲销的" + rs.getString("dt") + "已结案，限制反过账");
				}
			}
		}
	}

	/**
	 * 预测单审核的时，产生对应的预测冲预测的冲销单
	 * 
	 * @author ZhongYL
	 */
	@Override
	@Transactional
	public void createForeCastClash(Integer sf_id) {
		String SqlStr = "", clashcode = "";
		int csid = 0, detno = 0;
		String prodcode = "", fromcode = "";
		SqlStr = "select sf_id,sf_code,sd_detno,sd_forecastcode,sd_forecastdetno,sd_prodcode,sd_qty from saleforecast,saleforecastdetail where sf_id=sd_sfid and sf_id="
				+ sf_id
				+ " and sf_statuscode='AUDITED' and NVL(sd_statuscode,' ')<>'FINISH' and sd_forecastcode<>' ' and sd_forecastdetno>0";
		detno = 1;
		SqlRowList rs = baseDao.queryForRowSet(SqlStr);
		while (rs.next()) {
			if (detno == 1) {
				fromcode = rs.getString("sf_code");
				prodcode = rs.getString("sd_prodcode");
				// 判断此行记录是否需要冲销
				SqlStr = "SELECT * from SaleClash where sc_source='销售预测单' and sc_sourcecode='" + fromcode + "'";
				SqlRowList rs2 = baseDao.queryForRowSet(SqlStr);
				if (rs2.next()) {
					if (rs2.getString("sc_statuscode").equals("AUDITED")) {
						// 已经审核的冲销单不能删除。不能重复产生
						return;
					} else {
						baseDao.execute("delete from SaleClash where sc_id='" + rs2.getString("sc_id") + "' ;");
						baseDao.execute("delete from saleclashdetail where scd_scid='" + rs2.getString("sc_id") + "'");
					}
				}
				clashcode = baseDao.sGetMaxNumber("SaleClash", 2);
				csid = baseDao.getSeqId("SALECLASH_SEQ");
				SqlStr = "insert into SaleClash(sc_id,sc_code,sc_date,sc_status,sc_statuscode,sc_recorder,sc_source,sc_sourceid,sc_sourcecode)values("
						+ csid
						+ ",'"
						+ clashcode
						+ "',sysdate,'"
						+ BaseUtil.getLocalMessage("COMMITED")
						+ "','COMMITED','"
						+ SystemSession.getUser().getEm_name() + "','销售预测单'," + rs.getString("sf_id") + ",'" + fromcode + "')   ";
				baseDao.execute(SqlStr);
			}
			SqlStr = "insert into SaleClashDetail(scd_id,scd_scid,scd_detno,scd_prodcode,scd_clashqty,scd_ordercode,scd_orderdetno)values("
					+ "SALECLASHDETAIL_SEQ.NEXTVAL," + csid + ",'" + detno++ + "','" + prodcode + "','" + rs.getDouble("sd_qty") + "','"
					+ rs.getString("sd_forecastcode") + "','" + rs.getInt("sd_forecastdetno") + "')";
			baseDao.execute(SqlStr);
		}
		if (!"".equals(clashcode)) {
			// 审核冲销单
			auditSaleClash(csid, "SaleClash");
		}
	}
}

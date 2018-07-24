package com.uas.erp.service.drp.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.MoneyUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.common.SaleDao;

import com.uas.erp.model.MessageLog;
import com.uas.erp.service.drp.SaleAskService;

@Service
public class SaleAskServiceImpl implements SaleAskService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private SaleDao saleDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void deleteSaleAsk(int sa_id, String caller) {
		// 只能删除[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + sa_id);
		StateAssert.delOnlyEntering(status);
		// 执行删除前的其它逻辑
		handlerService.beforeDel(caller, sa_id);
		// 删除sale
		baseDao.deleteById("saleAsk", "sa_id", sa_id);
		// 删除saleAskDetail
		saleDao.deleteSale(sa_id);
		// 记录操作
		baseDao.logger.delete(caller, "sa_id", sa_id);
		// 执行删除后的其它逻辑
		handlerService.afterDel(caller, sa_id);
	}

	@Override
	public void saveSaleAsk(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("saleAsk",
				"sa_code='" + store.get("sa_code") + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("drp.distribution.saleAsk.save_sacodeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.beforeSave(caller, new Object[] { store, grid });
		store.put("sa_printstatuscode", "UNPRINT");
		store.put("sa_printstatus", BaseUtil.getLocalMessage("UNPRINT"));
		// 保存saleAsk
		String formSql = SqlUtil.getInsertSqlByFormStore(store, "SaleAsk",
				new String[] {}, new Object[] {});
		baseDao.execute(formSql);
		// 保存saleDetail
		Object[] sd_id = new Object[grid.size()];
		bool = store.get("sa_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		JSONObject obj = null;
		for (int i = 0; i < grid.size(); i++) {
			Map<Object, Object> map = grid.get(i);
			sd_id[i] = baseDao.getSeqId("SALEASKDETAIL_SEQ");
			map.put("sd_id", sd_id[i]);
			map.put("sd_statuscode", "ENTERING");
			map.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
			if (bool
					&& (map.get("sd_price") == null || Double.parseDouble(map
							.get("sd_price").toString()) == 0)) {
				// 到销售单价表取单价
				obj = saleDao.getSalePrice(
						String.valueOf(store.get("sa_custcode")),
						String.valueOf(map.get("sd_prodcode")),
						String.valueOf(store.get("sa_currency")),
						String.valueOf(store.get("sa_kind")));
				if (obj != null) {
					double price = obj.getDouble("sd_price");
					double tax = obj.getDouble("sd_taxrate");
					double p = NumberUtil.formatDouble(price, 6);
					double qty = Double.parseDouble(map.get("sd_qty")
							.toString());
					map.put("sd_price", p);
					map.put("sd_taxrate", tax);
					// 金额
					double total = qty * p;
					map.put("sd_total", NumberUtil.formatDouble(total, 3));
					// 不含税金额
					p = p / (1 + tax / 100);
					map.put("sd_costprice", NumberUtil.formatDouble(p, 6));
					map.put("sd_taxtotal", NumberUtil.formatDouble(qty * p, 2));
				} else {
					map.put("sd_total", 0);
					map.put("sd_costprice", 0);
					map.put("sd_taxtotal", 0);
					error.append("根据 客户编号:[" + store.get("sa_custcode")
							+ "],物料编号:[" + map.get("sd_prodcode") + "],币别:["
							+ store.get("sa_currency")
							+ "] 未找到对应单价，或单价为空值、0,或未审核等!<BR/>");
				}
			} else {
				// 金额
				double total = Double.parseDouble(map.get("sd_qty").toString())
						* Double.parseDouble(map.get("sd_price").toString());
				map.put("sd_total", NumberUtil.formatDouble(total, 3));
				// 不含税金额
				double price = Double.parseDouble(map.get("sd_price")
						.toString())
						/ (1 + Double.parseDouble(map.get("sd_taxrate")
								.toString()) / 100);
				map.put("sd_costprice", price);
				total = Double.parseDouble(map.get("sd_qty").toString())
						* price;
				map.put("sd_taxtotal", NumberUtil.formatDouble(total, 3));
			}
		}
		List<String> gridSql = SqlUtil.getInsertSqlbyGridStore(grid,
				"SaleAskDetail");
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update saleask set sa_total=(select sum(sd_total) from saleaskdetail where saleaskdetail.sd_said = saleask.sa_id)");
		Object total = baseDao.getFieldDataByCondition("SaleAsk", "sa_total",
				"sa_id=" + store.get("sa_id"));
		if (total != null) {
			baseDao.execute("update saleask set sa_totalupper='"
					+ MoneyUtil.toChinese(total.toString()) + "' WHERE sa_id="
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
	public void updateSaleAsk(String formStore, String gridStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil
				.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的订单资料!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + store.get("sa_id"));
		StateAssert.updateOnlyEntering(status);
		// 执行修改前的其它逻辑
		handlerService.beforeUpdate(caller, new Object[] { store, gstore });
		// 设置更新时间
		store.put("sa_updateman", SystemSession.getUser().getEm_name());
		store.put("sa_updatedate", DateUtil.currentDateString(Constant.YMD_HMS));
		// 更新saleAsk
		String formSql = SqlUtil.getUpdateSqlByFormStore(store, "SaleAsk",
				"sa_id");
		baseDao.execute(formSql);
		// 更新saleAskDetail
		boolean bool = store.get("sa_getprice").toString().equals("-1");// 是否自动获取单价
		StringBuffer error = new StringBuffer();
		JSONObject obj = null;
		for (Map<Object, Object> s : gstore) {
			if (bool) {
				// 到物料核价单取单价
				obj = saleDao.getSalePrice(
						String.valueOf(store.get("sa_custcode")),
						String.valueOf(s.get("sd_prodcode")),
						String.valueOf(store.get("sa_currency")),
						String.valueOf(store.get("sa_kind")));
				if (obj != null) {
					double price = obj.getDouble("sd_price");
					double tax = obj.getDouble("sd_taxrate");
					double p = NumberUtil.formatDouble(price, 6);
					double qty = Double.parseDouble(s.get("sd_qty").toString());
					s.put("sd_price", p);
					s.put("sd_taxrate", tax);
					// 金额
					double total = qty * p;
					s.put("sd_total", NumberUtil.formatDouble(total, 3));
					// 不含税金额
					p = p / (1 + tax / 100);
					s.put("sd_costprice", NumberUtil.formatDouble(p, 6));
					s.put("sd_taxtotal", NumberUtil.formatDouble(qty * p, 2));
				} else {
					s.put("sd_price", 0);
					s.put("sd_taxrate", 0);
					s.put("sd_total", 0);
					s.put("sd_costprice", 0);
					s.put("sd_taxtotal", 0);
					error.append("根据 客户编号:[" + store.get("sa_custcode")
							+ "],物料编号:[" + s.get("sd_prodcode") + "],币别:["
							+ store.get("sa_currency")
							+ "] 未找到对应单价，或单价为空值、0,或未审核等!<BR/>");
				}
			} else {
				// 金额
				double total = Double.parseDouble(s.get("sd_qty").toString())
						* Double.parseDouble(s.get("sd_price").toString());
				s.put("sd_total", NumberUtil.formatDouble(total, 3));
				// 不含税金额
				double price = Double.parseDouble(s.get("sd_price").toString())
						/ (1 + Double.parseDouble(s.get("sd_taxrate")
								.toString()) / 100);
				s.put("sd_costprice", price);
				total = Double.parseDouble(s.get("sd_qty").toString()) * price;
				s.put("sd_taxtotal", NumberUtil.formatDouble(total, 3));
			}
		}
		List<String> gridSql = SqlUtil.getUpdateSqlbyGridStore(gstore,
				"SaleAskDetail", "sd_id");
		for (Map<Object, Object> s : gstore) {
			Object sdid = s.get("sd_id");
			s.put("sd_total", Float.parseFloat(s.get("sd_qty").toString())
					* Double.parseDouble(s.get("sd_price").toString()));
			if (sdid == null || sdid.equals("") || sdid.equals("0")
					|| Integer.parseInt(sdid.toString()) == 0) {// 新添加的数据，id不存在
				int id = baseDao.getSeqId("SALEASKDETAIL_SEQ");
				s.put("sd_id", id);
				s.put("sd_statuscode", "ENTERING");
				s.put("sd_status", BaseUtil.getLocalMessage("ENTERING"));
				String sql = SqlUtil.getInsertSqlByMap(s, "SaleAskDetail",
						new String[] { "sd_id" }, new Object[] { id });
				gridSql.add(sql);
			}
		}
		baseDao.execute(gridSql);
		// 修改主单据的总金额
		baseDao.execute("update saleask set sa_total=(select sum(sd_total) from saleaskdetail where saleaskdetail.sd_said = saleask.sa_id)");
		Object total = baseDao.getFieldDataByCondition("SaleAsk", "sa_total",
				"sa_id=" + store.get("sa_id"));
		if (total != null) {
			baseDao.execute("update saleask set sa_totalupper='"
					+ MoneyUtil.toChinese(total.toString()) + "' WHERE sa_id="
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
	public void auditSaleAsk(int sa_id, String caller) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + sa_id);
		StateAssert.auditOnlyCommited(status);
		// 执行审核前的其它逻辑
		handlerService.beforeAudit(caller, sa_id);
		// 执行审核操作
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_statuscode='AUDITED',sa_status='"
						+ BaseUtil.getLocalMessage("AUDITED")
						+ "',sa_auditdate="
						+ DateUtil.parseDateToOracleString(Constant.YMD_HMS,
								new Date()) + ",sa_auditman='"
						+ SystemSession.getUser().getEm_name() + "'", "sa_id="
						+ sa_id);
		baseDao.updateByCondition(
				"SaleAskDetail",
				"sd_statuscode='AUDITED',sd_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "sd_said="
						+ sa_id);
		// 记录操作
		baseDao.logger.audit(caller, "sa_id", sa_id);
		// 执行审核后的其它逻辑
		handlerService.afterAudit(caller, sa_id);
	}

	@Override
	public void resAuditSaleAsk(int sa_id, String caller) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] objs = baseDao.getFieldsDataByCondition("SaleAsk",
				new String[] { "sa_statuscode", "sa_turnstatuscode",
						"sa_sendstatuscode" }, "sa_id=" + sa_id);
		if (!String.valueOf(objs[0]).equals("AUDITED")
				|| String.valueOf(objs[1]).equals("TURNSN")
				|| String.valueOf(objs[1]).equals("PART2SN")
				|| String.valueOf(objs[2]).equals("TURNOUT")
				|| String.valueOf(objs[2]).equals("PARTOUT")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("drp.distribution.saleask.resaudit_onlyAudited"));
		}
		/*
		 * //已有发货通知单号 Object code = baseDao.getFieldDataByCondition("Sale",
		 * "sa_code", "sa_id=" + sa_id); String str =
		 * saleDao.checkSendNotify((String)code); if(str != null){
		 * BaseUtil.showError
		 * (BaseUtil.getLocalMessage("scm.sale.sale.resaudit_sndcodeHasExist",
		 * language) + str); } //已有发货单 str =
		 * saleDao.checkProdInOut((String)code); if(str != null){
		 * BaseUtil.showError
		 * (BaseUtil.getLocalMessage("scm.sale.sale.resaudit_pdcodeHasExist",
		 * language) + str); } //已有制造单 Object obj =
		 * baseDao.getFieldDataByCondition("Make", "ma_code",
		 * "ma_statuscode<>'DELETED' AND ma_salecode='" + code + "'"); if(obj !=
		 * null && !obj.equals("")){
		 * BaseUtil.showError(BaseUtil.getLocalMessage(
		 * "scm.sale.sale.resaudit_macodeHasExist") + obj); } //已有发货数不能反审核 obj =
		 * baseDao.getFieldDataByCondition("SaleDetail", "sd_detno",
		 * "isnull(sd_sendqty,0)>0 AND sd_said=" + sa_id); if(obj != null &&
		 * !obj.equals("")){ BaseUtil.showError(BaseUtil.getLocalMessage(
		 * "scm.sale.sale.resaudit_qtyHasExist") + obj); }
		 */
		handlerService.beforeResAudit(caller, sa_id);
		// 反审核操作
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_statuscode='ENTERING',sa_status='"
						+ BaseUtil.getLocalMessage("ENTERING")
						+ "',SA_AUDITMAN='',SA_AUDITdate=null", "sa_id="
						+ sa_id);
		baseDao.updateByCondition(
				"SaleAskDetail",
				"sd_statuscode='ENTERING',sd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sd_said=" + sa_id);
		// 记录操作
		baseDao.logger.resAudit(caller, "sa_id", sa_id);
		handlerService.afterResAudit(caller, sa_id);
	}

	@Override
	public void submitSaleAsk(int sa_id, String caller) {
		// 只能提交状态为[在录入]的合同!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + sa_id);
		StateAssert.submitOnlyEntering(status);
		handlerService.beforeSubmit(caller, sa_id);
		// 判断客户是否未审核或已禁用
		Object cust = baseDao.getFieldDataByCondition("SaleAsk", "sa_custcode",
				"sa_id=" + sa_id);
		status = baseDao.getFieldDataByCondition("Customer",
				"cu_auditstatuscode", "cu_code='" + cust + "'");
		if (status != null && status.equals("AUDITED")) {
			// 判断订单明细是否填写数量
			boolean bool = baseDao.checkByCondition("SaleAskDetail", "sd_said="
					+ sa_id + " AND (sd_qty is null OR sd_qty=0)");
			if (bool) {
				// 执行提交前的其它逻辑
				handlerService.handler("SaleAsk", "commit", "before",
						new Object[] { sa_id, SystemSession.getUser() });
				// 执行提交操作
				baseDao.updateByCondition(
						"SaleAsk",
						"sa_statuscode='COMMITED',sa_status='"
								+ BaseUtil.getLocalMessage("COMMITED") + "'",
						"sa_id=" + sa_id);
				baseDao.updateByCondition(
						"SaleAskDetail",
						"sd_statuscode='COMMITED',sd_status='"
								+ BaseUtil.getLocalMessage("COMMITED") + "'",
						"sd_said=" + sa_id);
				// 记录操作
				baseDao.logger.submit(caller, "sa_id", sa_id);
				// 修改主单据的总金额
				baseDao.execute("update saleask set sa_total=(select sum(sd_total) from saleaskdetail where saleaskdetail.sd_said = saleask.sa_id)");
				Object total = baseDao.getFieldDataByCondition("SaleAsk",
						"sa_total", "sa_id=" + sa_id);
				if (total != null) {
					baseDao.execute("update saleask set sa_totalupper='"
							+ MoneyUtil.toChinese(total.toString())
							+ "' WHERE sa_id=" + sa_id);
				}
				// 执行提交后的其它逻辑
				handlerService.afterSubmit(caller, sa_id);
			} else {
				BaseUtil.showError("存在未填写数量的订单明细!");
			}
		} else {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("drp.distribution.saleask.submit_cust")
					+ "<a href=\"javascript:openUrl('jsps/drp/distribution/customerBase.jsp?formCondition=cu_codeIS"
					+ cust + "')\">" + cust + "</a>&nbsp;");
		}
	}

	@Override
	public void resSubmitSaleAsk(int sa_id, String caller) {
		// 只能对状态为[已提交]的合同进行反提交
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + sa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.beforeResSubmit(caller, sa_id);
		// 执行反提交操作
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_statuscode='ENTERING',sa_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'", "sa_id="
						+ sa_id);
		baseDao.updateByCondition(
				"SaleAskDetail",
				"sd_statuscode='ENTERING',sd_status='"
						+ BaseUtil.getLocalMessage("ENTERING") + "'",
				"sd_said=" + sa_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "sa_id", sa_id);
		handlerService.afterResSubmit(caller, sa_id);
	}

	@Override
	public String[] printSaleAsk(int sa_id, String caller, String reportName,
			String condition) {
		// 执行打印前的其它逻辑
		handlerService.handler("SaleAsk", "print", "before",
				new Object[] { sa_id });
		// 执行打印操作
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 修改打印状态
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_printstatuscode='PRINTED',sa_printstatus='"
						+ BaseUtil.getLocalMessage("PRINTED") + "'", "sa_id="
						+ sa_id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.print"), BaseUtil
						.getLocalMessage("msg.printSuccess"), "SaleAsk|sa_id="
						+ sa_id));
		// 执行打印后的其它逻辑
		handlerService.handler("SaleAsk", "print", "after",
				new Object[] { sa_id });
		return keys;
	}

	@Override
	public void endSaleAsk(int id, String caller) {
		// 只能对状态为[已审核]的订单进行结案操作!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + id);
		if (!status.equals("AUDITED")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.end_onlyAudited"));
		}
		// 结案
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_statuscode='FINISH',sa_status='"
						+ BaseUtil.getLocalMessage("FINISH") + "'", "sa_id="
						+ id);
		baseDao.updateByCondition(
				"SaleAskDetail",
				"sd_statuscode='FINISH',sd_status='"
						+ BaseUtil.getLocalMessage("FINISH") + "'", "sd_said="
						+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.end"), BaseUtil
						.getLocalMessage("msg.endSuccess"), "SaleAsk|sa_id="
						+ id));
	}

	@Override
	public void resEndSaleAsk(int id, String caller) {
		// 只能对状态为[已结案]的订单进行反结案操作!
		Object status = baseDao.getFieldDataByCondition("SaleAsk",
				"sa_statuscode", "sa_id=" + id);
		if (!status.equals("FINISH")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("common.resEnd_onlyEnd"));
		}
		// 反结案
		baseDao.updateByCondition(
				"SaleAsk",
				"sa_statuscode='AUDITED',sa_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "sa_id="
						+ id);
		baseDao.updateByCondition(
				"SaleAskDetail",
				"sd_statuscode='AUDITED',sd_status='"
						+ BaseUtil.getLocalMessage("AUDITED") + "'", "sd_said="
						+ id);
		// 记录操作
		baseDao.logMessage(new MessageLog(SystemSession.getUser().getEm_name(),
				BaseUtil.getLocalMessage("msg.resEnd"), BaseUtil
						.getLocalMessage("msg.resEndSuccess"), "SaleAsk|sa_id="
						+ id));
	}

	public void getPrice(int sa_id) {
		saleDao.getPrice(sa_id);
	}

	@Override
	public int turnSendNotify(int sa_id, String caller) {
		int snid = 0;
		// 判断该销售单是否已经转入过转发货通知单
		Object code = baseDao.getFieldDataByCondition("SaleAskDetail",
				"sd_code", "sd_said=" + sa_id);
		code = baseDao.getFieldDataByCondition("sendNotifyDetail", "snd_code",
				"snd_sourcecode='" + code + "'");
		if (code != null && !code.equals("")) {
			BaseUtil.showError(BaseUtil
					.getLocalMessage("drp.distribution.saleask.haveturn")
					+ "<a href=\"javascript:openUrl('jsps/drp/distribution/sendNotify.jsp?formCondition=sn_codeIS"
					+ code
					+ "&gridCondition=snd_codeIS"
					+ code
					+ "')\">"
					+ code + "</a>&nbsp;");
		} else {
			// 转发货通知单
			snid = saleDao.turnSendNotify(sa_id);
			// 修改销售单状态
			baseDao.updateByCondition(
					"SaleAsk",
					"sa_turnstatuscode='TURNSA',sa_turnstatus='"
							+ BaseUtil.getLocalMessage("TURNSA") + "'",
					"sa_id=" + sa_id);
			baseDao.updateByCondition(
					"SaleAskDetail",
					"sd_statuscode='TURNSA',sd_status='"
							+ BaseUtil.getLocalMessage("TURNSA")
							+ "',sd_yqty=sd_yqty", "sd_said=" + sa_id);
			// 记录操作
			baseDao.logMessage(new MessageLog(SystemSession.getUser()
					.getEm_name(), BaseUtil.getLocalMessage(
					"msg.turnSendNotify", SystemSession.getLang()), BaseUtil
					.getLocalMessage("msg.turnSuccess"), "SaleAsk|sa_id="
					+ sa_id));
		}
		return snid;
	}

}

package com.uas.erp.service.fa.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.JacksonUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.TransferRepository;
import com.uas.erp.dao.common.APCheckDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Key;
import com.uas.erp.service.fa.APCheckService;

@Service("apCheckService")
public class APCheckServiceImpl implements APCheckService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private APCheckDao apCheckDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private TransferRepository transferRepository;

	@Override
	public void saveAPCheck(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		String code = store.get("ac_code").toString();
		// 当前编号的记录已经存在,不能新增!
		boolean bool = baseDao.checkByCondition("APCheck", "ac_code='" + code + "'");
		if (!bool) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.save_codeHasExist"));
		}
		// 执行保存前的其它逻辑
		handlerService.handler("APCheck", "save", "before", new Object[] { formStore, gridStore });
		// 保存APCheck
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "APCheck"));
		baseDao.execute(SqlUtil.getInsertSqlbyList(grid, "APCheckDetail", "ad_id"));
		baseDao.logger.save("APCheck", "ac_id", store.get("ac_id"));
		// 执行保存后的其它逻辑
		handlerService.handler("APCheck", "save", "after", new Object[] { formStore, gridStore });
	}

	@Override
	public void deleteAPCheck(int ac_id) {
		// 只能删除在录入的单据!
		Object status = baseDao.getFieldDataByCondition("APCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.delete_onlyEntering"));
		}
		apCheckDao.deleteAPCheck(ac_id);
		// 执行删除前的其它逻辑
		handlerService.handler("APCheck", "delete", "before", new Object[] { ac_id });
		// 删除APCheck
		baseDao.deleteById("APCheck", "ac_id", ac_id);
		// 删除APCheckDetail
		baseDao.deleteById("APCheckdetail", "ad_acid", ac_id);
		// 记录操作
		baseDao.logger.delete("APCheck", "ac_id", ac_id);
		// 执行删除后的其它逻辑
		handlerService.handler("APCheck", "delete", "after", new Object[] { ac_id });
	}

	@Override
	public void updateAPCheckById(String formStore, String gridStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> gstore = BaseUtil.parseGridStoreToMaps(gridStore);
		// 只能修改[在录入]的资料!
		Object status = baseDao.getFieldDataByCondition("APCheck", "ac_statuscode", "ac_id=" + store.get("ac_id"));
		if (!status.equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.update_onlyEntering"));
		}
		// 执行修改前的其它逻辑
		handlerService.handler("APCheck", "save", "before", new Object[] { store, gstore });
		// 修改APCheck
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "APCheck", "ac_id"));
		Object adid = null;
		Object sourcetype = null;
		Object qty = null;
		Integer sourcedetailid = null;
		double tQty = 0;// 收料通知单修改数量
		SqlRowList rs = null;
		boolean type = baseDao.isDBSetting("autoCreateApBill");
		String checksql1 = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_id=? and abs(abd_qty)<?";
		String checksql3 = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_pdid=? and abs(abd_qty)<?";
		String checksql2 = "SELECT pd_inoutno,pd_pdno,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty,pd_piclass FROM ProdIODetail WHERE pd_id=? and abs(nvl(pd_inqty,0)+nvl(pd_outqty,0))<?";
		for (Map<Object, Object> s : gstore) {
			adid = s.get("ad_id");
			tQty = Double.parseDouble(s.get("ad_qty").toString());
			if (adid != null && Integer.parseInt(adid.toString()) != 0) {
				Object[] objs = baseDao.getFieldsDataByCondition("APCheckDetail", new String[] { "nvl(ad_sourcedetailid,0)",
						"nvl(ad_qty,0)", "ad_sourcetype" }, "ad_id=" + adid + " and nvl(ad_sourcedetailid,0)<>0");
				if (objs != null && objs[0] != null) {
					sourcedetailid = Integer.parseInt(String.valueOf(objs[0]));
					sourcetype = String.valueOf(objs[2]);
					if (sourcedetailid != null && sourcedetailid > 0) {
						if (type) {
							if ("APBILL".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("APCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='APBILL' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql1, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应付发票号:").append(rs.getString("abd_code"))
											.append(",行号:").append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("APBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"abd_id=" + sourcedetailid);
							} else if ("PRODINOUT".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("APCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='PRODINOUT' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql3, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应付发票号:").append(rs.getString("abd_code"))
											.append(",行号:").append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("APBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"abd_pdid=" + sourcedetailid);
							}
						} else {
							if ("PRODINOUT".equals(sourcetype)) {
								qty = baseDao.getFieldDataByCondition("APCheckDetail", "sum(nvl(ad_qty,0))", "ad_sourcedetailid="
										+ sourcedetailid + "and nvl(ad_sourcetype,' ')='PRODINOUT' AND ad_id <>" + adid);
								qty = qty == null ? 0 : qty;
								rs = baseDao.queryForRowSet(checksql2, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],").append(rs.getString("pd_piclass")).append("号:")
											.append(rs.getString("pd_inoutno")).append(",行号:").append(rs.getInt("pd_pdno")).append(",数量:")
											.append(rs.getDouble("pd_qty")).append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("ProdIODetail", "pd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"pd_id=" + sourcedetailid);
							}
						}
					}
				}
			}
		}
		// 修改APCheckDetail
		baseDao.execute(SqlUtil.getInsertOrUpdateSql(gstore, "APCheckDetail", "ad_id"));
		baseDao.execute("update APCheckDetail set ad_sourcedetailid=(select abd_id from apbilldetail where abd_code=ad_sourcecode and abd_detno=ad_sourcedetno) where ad_acid="
				+ store.get("ac_id") + " and nvl(ad_sourcedetailid,0)=0 and ad_sourcetype='APBILL'");
		baseDao.execute("update APCheckDetail set ad_b2bamount=round(nvl(ad_b2bamount,0),2) where ad_acid=" + store.get("ac_id"));
		baseDao.execute("update APCheckDetail set ad_amount=round(nvl(ad_qty*round(ad_price,8),0),2) where ad_acid=" + store.get("ac_id"));
		baseDao.execute("update APCHECK set ac_checkamount=(select sum(ad_amount) from APCHECKDETAIL where ad_acid=ac_id) where ac_id="
				+ store.get("ac_id"));

		// 记录操作
		baseDao.logger.update("APCheck", "ac_id", store.get("ac_id"));
		// 执行修改后的其它逻辑
		handlerService.handler("APCheck", "save", "after", new Object[] { store, gstore });
	}

	@Override
	public String[] printAPCheck(int ac_id, String reportName, String condition) {
		String key = "12345678";
		String[] keys = BaseUtil.reportEncrypt(key, reportName, condition);
		// 执行打印前的其它逻辑
		handlerService.handler("APCheck", "print", "before", new Object[] { ac_id });
		// 执行打印操作
		// 记录操作
		baseDao.logger.print("APCheck", "ac_id", ac_id);
		// 执行打印后的其它逻辑
		handlerService.handler("APCheck", "print", "after", new Object[] { ac_id });
		return keys;
	}

	@Override
	public void auditAPCheck(int ac_id) {
		// 只能对状态为[已提交]的订单进行审核操作!
		Object status = baseDao.getFieldDataByCondition("APCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.audit_onlyCommited"));
		}
		baseDao.execute("update APCheckDetail set ad_b2bamount=round(nvl(ad_b2bamount,0),2) where ad_acid=" + ac_id);
		// 执行审核前的其它逻辑
		handlerService.handler("APCheck", "audit", "before", new Object[] { ac_id });
		// 执行审核操作
		baseDao.updateByCondition("APCheck", "ac_statuscode='AUDITED',ac_status='" + BaseUtil.getLocalMessage("AUDITED") + "'", "ac_id="
				+ ac_id);
		// 审核之后自动确认
		if (baseDao.isDBSetting("APCheck", "autoConfirm")) {
			confirmAPCheck(ac_id);
		}
		// 记录操作
		baseDao.logger.audit("APCheck", "ac_id", ac_id);
		// 执行审核后的其它逻辑
		handlerService.handler("APCheck", "audit", "after", new Object[] { ac_id });
	}

	@Override
	public void resAuditAPCheck(int ac_id) {
		// 只能对状态为[已审核]的订单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("APCheck",
				new String[] { "ac_statuscode", "nvl(ac_b2bid,0)", "ac_confirmstatus" }, "ac_id=" + ac_id);
		if (!status[0].equals("AUDITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAudit_onlyAudit"));
		}
		if (status[1] != null && Integer.parseInt(status[1].toString()) != 0) {
			BaseUtil.showError("供应商平台传过来的单据不允许反审核！");
		}
		if (status[2] != null && "已确认".equals(status[2].toString())) {
			BaseUtil.showError("当前对账单已确认，如果需要反审核请先取消确认！");
		}
		if (baseDao.isDBSetting("autoCreateApBill")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ad_detno) from APCheckDetail left join billoutapdetail on ad_id = ard_adid where nvl(ard_adid,0)<>0 and ad_acid=?",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("明细行已转开票记录，不允许反审核！行号：" + dets);
			}
		} else {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ad_detno) from APCheckDetail left join apbilldetail on ad_id = abd_adid where nvl(abd_adid,0)<>0 and ad_acid=?",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("明细行已转应付发票，不允许反审核！行号：" + dets);
			}
		}
		// 执行反审核操作
		baseDao.updateByCondition("APCheck", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ac_id="
				+ ac_id);
		// 记录操作
		baseDao.logger.resAudit("APCheck", "ac_id", ac_id);
	}

	@Override
	public void submitAPCheck(int ac_id) {
		// 只能对状态为[在录入]的订单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("APCheck", new String[] { "ac_statuscode", "ac_fromdate", "ac_todate" },
				"ac_id=" + ac_id);
		if (!status[0].equals("ENTERING")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.submit_onlyEntering"));
		}

		baseDao.execute("update APCheckDetail set ad_b2bamount=round(nvl(ad_b2bamount,0),2) where ad_acid=" + ac_id);
		// 根据明细重新计算对账金额
		baseDao.execute("update APCHECK set ac_checkamount=(select sum(ad_amount) from APCHECKDETAIL where ad_acid=ac_id) where ac_id="
				+ ac_id);

		if (status[1] == null) {
			BaseUtil.showError("对账起始日期不能为空！");
		}
		if (status[2] == null) {
			BaseUtil.showError("对账截至日期不能为空！");
		}
		if (baseDao.isDBSetting("APCheck", "APARCheckAccount")) {
			baseDao.procedure("SP_APCHECKBEGIN", new Object[] { ac_id });
		}
		// 执行提交前的其它逻辑
		handlerService.handler("APCheck", "commit", "before", new Object[] { ac_id });
		// 执行提交操作
		baseDao.updateByCondition("APCheck", "ac_statuscode='COMMITED',ac_status='" + BaseUtil.getLocalMessage("COMMITED") + "'", "ac_id="
				+ ac_id);
		// 记录操作
		baseDao.logger.submit("APCheck", "ac_id", ac_id);
		// 执行提交后的其它逻辑
		handlerService.handler("APCheck", "commit", "after", new Object[] { ac_id });
	}

	@Override
	public void resSubmitAPCheck(int ac_id) {
		// 只能对状态为[已提交]的订单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("APCheck", "ac_statuscode", "ac_id=" + ac_id);
		if (!status.equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resSubmit_onlyCommited"));
		}
		handlerService.handler("APCheck", "resCommit", "before", new Object[] { ac_id });
		// 执行反提交操作
		baseDao.updateByCondition("APCheck", "ac_statuscode='ENTERING',ac_status='" + BaseUtil.getLocalMessage("ENTERING") + "'", "ac_id="
				+ ac_id);
		// 记录操作
		baseDao.logger.resSubmit("APCheck", "ac_id", ac_id);
		handlerService.handler("APCheck", "resCommit", "after", new Object[] { ac_id });
	}

	@Override
	public void accountedAPCheck(int ac_id) {
		// 只能对状态为[未记账]的订单进行操作!
		Object[] status = baseDao.getFieldsDataByCondition("APCheck", new String[] { "ac_statuscode", "ac_code" }, "ac_id=" + ac_id);
		if (!status[0].equals("AUDITED") && !status[0].equals("COMMITED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.account_onlyCommited"));
		}
		// 执行记账前的其它逻辑
		handlerService.handler("APCheck", "account", "before", new Object[] { ac_id });
		// 执行记账操作
		// 存储过程
		String res = baseDao.callProcedure("SP_COMMITEAPCHECK", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("APCheck", "ac_statuscode='POSTED',ac_status='已过账',ac_postdate=sysdate,ac_postman='"
				+ SystemSession.getUser().getEm_name() + "'", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.others("msg.account", "msg.accountSuccess", "APCheck", "ac_id", ac_id);
		// 执行记账后的其它逻辑
		handlerService.handler("APCheck", "account", "after", new Object[] { ac_id });
	}

	@Override
	public void resAccountedAPCheck(int ac_id) {
		// 只能对状态为[已记账]的订单进行反记账操作!
		Object[] status = baseDao.getFieldsDataByCondition("APCheck", new String[] { "ac_statuscode", "ac_code" }, "ac_id=" + ac_id);
		if (!status[0].equals("POSTED")) {
			BaseUtil.showError(BaseUtil.getLocalMessage("common.resAccount_onlyAccount"));
		}
		if (baseDao.isDBSetting("autoCreateApBill")) {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ad_detno) from APCheckDetail left join billoutapdetail on ad_id = ard_adid where nvl(ard_adid,0)<>0 and ad_acid=?",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("明细行已转开票记录，不允许反记账！行号：" + dets);
			}
		} else {
			String dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(ad_detno) from APCheckDetail left join apbilldetail on ad_id = abd_adid where nvl(abd_adid,0)<>0 and ad_acid=?",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("明细行已转应付发票，不允许反记账！行号：" + dets);
			}
		}
		// 执行反记账操作
		String res = baseDao.callProcedure("SP_UNCOMMITEAPCHECK", new Object[] { status[1] });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		baseDao.updateByCondition("APCheck", "ac_statuscode='AUDITED',ac_status='" + BaseUtil.getLocalMessage("AUDITED")
				+ "',ac_postdate=null,ac_postman=null", "ac_id=" + ac_id);
		// 记录操作
		baseDao.logger.others("msg.resAccount", "msg.resAccountSuccess", "APCheck", "ac_id", ac_id);
	}

	@Override
	public void confirmAPCheck(int ac_id) {
		Long adid = null;
		Object sourcetype = null;
		Object qty = null;
		Long sourcedetailid = null;
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select str_concat(ac_code) from apcheck where ac_id=? and ac_confirmstatus='不同意'", String.class, ac_id);
		if (dets != null) {
			BaseUtil.showError("确认状态是[不同意]！不允许进行确认操作！");
		}
		if (baseDao.isDBSetting("autoCreateApBill") && baseDao.isDBSetting("APCheck", "priceCheck")) {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from apcheckdetail,apbilldetail,apbill where ad_pdid=abd_pdid and abd_abid=ab_id and ad_acid=? and ab_class='应付发票' and nvl(ad_sourcetype,' ')='PRODINOUT' and (nvl(ad_price,0)<>nvl(abd_thisvoprice,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0)) and exists (select 1 from apbilldetail where ad_pdid=abd_pdid)",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与应付发票不一致！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from apcheckdetail,apbilldetail,apbill where ad_sourcedetailid=abd_id and abd_abid=ab_id and ad_acid=? and ab_class='应付发票' and nvl(ad_sourcetype,' ')='APBILL' and (nvl(ad_price,0)<>nvl(abd_thisvoprice,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0))",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与应付发票不一致！行号：" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select str_concat(ad_detno) from apcheckdetail,apbilldetail,apbill where ad_sourcedetailid=abd_id and abd_abid=ab_id and ad_acid=? and ab_class='其它应付单' and nvl(ad_sourcetype,' ')='APBILL' and (nvl(ad_price,0)<>nvl(abd_price,0) or nvl(ad_taxrate,0)<>nvl(abd_taxrate,0))",
							String.class, ac_id);
			if (dets != null) {
				BaseUtil.showError("单价、税率与其它应付单不一致！行号：" + dets);
			}
		}
		double tQty = 0;
		Object acb2b = baseDao.getFieldDataByCondition("APCheck", "nvl(ac_b2bid,0)", "ac_id=" + ac_id);
		if (acb2b != null && Integer.parseInt(acb2b.toString()) != 0) {
			SqlRowList rs = null;
			String checksql1 = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_id=? and abs(abd_qty)<?";
			String checksql2 = "SELECT pd_inoutno,pd_pdno,nvl(pd_inqty,0)+nvl(pd_outqty,0) pd_qty,pd_piclass FROM ProdIODetail WHERE pd_id=? and abs(nvl(pd_inqty,0)+nvl(pd_outqty,0))<?";
			String checksql3 = "SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE abd_sourcedetailid=? and abd_sourcekind='PRODIODETAIL' and abs(abd_qty)<?";
			baseDao.execute("update APCheckDetail set ad_sourcedetailid=ad_pdid where ad_acid=" + ac_id + " and nvl(ad_sourcedetailid,0)=0");
			SqlRowList ac = baseDao.queryForRowSet(
					"select ad_b2bqty, ad_id, ad_sourcedetailid, ad_sourcetype from APCheckDetail where ad_acid=?", ac_id);
			while (ac.next()) {
				adid = ac.getGeneralLong("ad_id");
				tQty = ac.getGeneralDouble("ad_b2bqty");
				sourcedetailid = ac.getGeneralLong("ad_sourcedetailid");
				if (adid != 0) {
					if (sourcedetailid != 0) {
						sourcetype = ac.getObject("ad_sourcetype");
						if ("APBILL".equals(sourcetype)) {
							qty = baseDao.getFieldDataByCondition("APCheck left join APCheckDetail on ac_id=ad_acid",
									"sum(nvl(ad_b2bqty,0))", "ad_sourcedetailid=" + sourcedetailid
											+ "and nvl(ad_sourcetype,' ')='APBILL' and nvl(AC_CONFIRMSTATUS,' ')<>'不同意' AND ad_id <>"
											+ adid);
							qty = qty == null ? 0 : qty;
							rs = baseDao.queryForRowSet(checksql1, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
							if (rs.next()) {
								StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应付发票号:").append(rs.getString("abd_code")).append(",行号:")
										.append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty")).append(",已转数量:")
										.append(qty).append(",本次数量:").append(tQty);
								BaseUtil.showError(sb.toString());
							}
							baseDao.updateByCondition("APBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
									"abd_id=" + sourcedetailid);
						} else if ("PRODINOUT".equals(sourcetype)) {
							qty = baseDao.getFieldDataByCondition("APCheck left join APCheckDetail on ac_id=ad_acid",
									"sum(nvl(ad_b2bqty,0))", "ad_sourcedetailid=" + sourcedetailid
											+ " and nvl(ad_sourcetype,' ')='PRODINOUT' and nvl(AC_CONFIRMSTATUS,' ')<>'不同意' AND ad_id <>"
											+ adid);
							qty = qty == null ? 0 : qty;
							if (baseDao.isDBSetting("autoCreateApBill")) {
								rs = baseDao.queryForRowSet(checksql3, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],应付发票号:").append(rs.getString("abd_code"))
											.append(",行号:").append(rs.getInt("abd_detno")).append(",发票数量:").append(rs.getDouble("abd_qty"))
											.append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("APBillDetail", "abd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"abd_sourcedetailid=" + sourcedetailid);
							} else {
								rs = baseDao.queryForRowSet(checksql2, sourcedetailid, Math.abs(Double.parseDouble(qty.toString()) + tQty));
								if (rs.next()) {
									StringBuffer sb = new StringBuffer("[本次数量填写超出可转数量],").append(rs.getString("pd_piclass")).append("号:")
											.append(rs.getString("pd_inoutno")).append(",行号:").append(rs.getInt("pd_pdno")).append(",数量:")
											.append(rs.getDouble("pd_qty")).append(",已转数量:").append(qty).append(",本次数量:").append(tQty);
									BaseUtil.showError(sb.toString());
								}
								baseDao.updateByCondition("ProdIODetail", "pd_ycheck=" + (Double.parseDouble(String.valueOf(qty)) + tQty),
										"pd_id=" + sourcedetailid);
							}
						}
					}
				}
			}
			baseDao.updateByCondition("APCheckDetail",
					"ad_qty=nvl(ad_b2bqty,0),ad_amount=round(nvl(ad_b2bqty,0)*round(nvl(ad_price,0),8),2)", "ad_acid=" + ac_id);
			baseDao.execute("update APCheck set ac_checkamount =(select round(sum(nvl(ad_amount,0)),2) from APCheckDetail where ad_acid=ac_id) where ac_id="
					+ ac_id);
			baseDao.updateByCondition("APCheck", "ac_confirmstatus='已确认', ac_confirmdate=sysdate, ac_sendstatus='待上传'", "ac_id=" + ac_id);
			baseDao.execute("update prodiodetail set pd_ycheck=nvl((select sum(ad_qty) from apcheck,apcheckdetail where ac_id=ad_acid and nvl(ac_confirmstatus,' ')='已确认' and ad_sourcetype='PRODINOUT' and ad_pdid=pd_id),0) where exists (select ad_pdid from apcheckdetail where pd_id=ad_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='PRODINOUT')");
			baseDao.execute("update apbilldetail set abd_ycheck=nvl((select sum(ad_qty) from apcheck,apcheckdetail where ac_id=ad_acid and nvl(ac_confirmstatus,' ')='已确认' and ad_sourcetype='APBILL' and ad_pdid=abd_pdid),0) where exists (select ad_pdid from apcheckdetail where abd_pdid=ad_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='APBILL')");

		} else {
			baseDao.updateByCondition("APCheck", "ac_confirmstatus='已确认', ac_confirmdate=sysdate, ac_sendstatus='待上传'", "ac_id=" + ac_id);
		}
		// 记录操作
		baseDao.logger.others("确认对账", "确认成功", "APCheck", "ac_id", ac_id);
		// 确认之后自动开票
		if (baseDao.isDBSetting("APCheck", "autoBill")) {
			List<Map<Object, Object>> details = baseDao.getJdbcTemplate().query(
					"select ad_qty,ad_id,ad_sourcetype from APCheckDetail where ad_acid=?", new RowMapper<Map<Object, Object>>() {

						@Override
						public Map<Object, Object> mapRow(ResultSet rs, int index) throws SQLException {
							Map<Object, Object> map = new HashMap<Object, Object>();
							map.put("ad_qty", rs.getObject("ad_qty"));
							map.put("ad_tqty", rs.getObject("ad_qty"));
							map.put("ad_sourcetype", rs.getObject("ad_sourcetype"));
							map.put("ad_id", rs.getObject("ad_id"));
							return map;
						}

					}, ac_id);
			turnBill("APCheck!ToBill!Deal", details);
		}
	}

	@Override
	public void cancelAPCheck(int ac_id) {
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(ad_detno) from apcheckdetail where ad_acid=? and nvl(ad_yqty,0)>0", String.class, ac_id);
		if (dets != null) {
			if (baseDao.isDBSetting("APCheck", "autoBill")) {
				BaseUtil.showError("已确认开票，不允许取消确认！行号：" + dets);
			} else {
				BaseUtil.appendError("已确认开票，不允许取消确认！行号：" + dets);
			}
		}
		Object acb2b = baseDao.getFieldDataByCondition("APCheck", "nvl(ac_b2bid,0)", "ac_id=" + ac_id);
		if (acb2b != null && Integer.parseInt(acb2b.toString()) != 0) {
			baseDao.updateByCondition("APCheck", "ac_confirmstatus=null, ac_confirmdate=null", "ac_id=" + ac_id);
			baseDao.updateByCondition("APCheckDetail", "ad_qty=0, ad_amount=0", "ad_acid=" + ac_id);
			baseDao.execute("update APCheck set ac_checkamount=0 where ac_id=" + ac_id);
			baseDao.execute("update prodiodetail set pd_ycheck=nvl((select sum(ad_qty) from apcheck,apcheckdetail where ac_id=ad_acid and nvl(ac_confirmstatus,' ')='已确认' and ad_sourcetype='PRODINOUT' and ad_pdid=pd_id),0) where exists (select ad_pdid from apcheckdetail where pd_id=ad_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='PRODINOUT')");
			baseDao.execute("update apbilldetail set abd_ycheck=nvl((select sum(ad_qty) from apcheck,apcheckdetail where ac_id=ad_acid and nvl(ac_confirmstatus,' ')='已确认' and ad_sourcetype='APBILL' and ad_pdid=abd_pdid),0) where exists (select ad_pdid from apcheckdetail where abd_pdid=ad_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='APBILL')");

		} else {
			baseDao.updateByCondition("APCheck", "ac_confirmstatus=null, ac_confirmdate=null", "ac_id=" + ac_id);
		}
		// 记录操作
		baseDao.logger.others("取消确认", "取消成功", "APCheck", "ac_id", ac_id);
	}

	@Override
	public void resConfirmAPCheck(int ac_id, String reason) {
		baseDao.updateByCondition("APCheck", "ac_confirmstatus='不同意',ac_reason='" + reason
				+ "', ac_confirmdate=sysdate ,ac_sendstatus='待上传'", "ac_id=" + ac_id);
		Object acb2b = baseDao.getFieldDataByCondition("APCheck", "nvl(ac_b2bid,0)", "ac_id=" + ac_id);
		if (acb2b != null && Integer.parseInt(acb2b.toString()) != 0) {
			baseDao.execute("update APBillDetail set abd_ycheck=nvl((select sum(ad_qty) from apcheckdetail where abd_pdid=ad_pdid and ad_sourcetype='PRODINOUT'),0) where exists (select ad_pdid from apcheckdetail where ad_pdid=abd_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='PRODINOUT')");
			baseDao.execute("update prodiodetail set pd_ycheck=nvl((select sum(ad_qty) from apcheckdetail where ad_pdid=pd_id and ad_sourcetype='APBILL'),0) where exists (select ad_pdid from apcheckdetail where pd_id=ad_pdid and ad_acid="
					+ ac_id + " and ad_sourcetype='APBILL')");
		}
		baseDao.logger.others("不同意对账", "不同意", "APCheck", "ac_id", ac_id);
	}

	@Override
	public String turnBill(String caller, String data) {
		List<Map<Object, Object>> maps = JacksonUtil.fromJsonArray(data);
		return turnBill(caller, maps);
	}

	public String turnBill(String caller, List<Map<Object, Object>> maps) {
		StringBuffer sb = new StringBuffer();
		Object y = 0;
		Object sourceqty = 0;
		SqlRowList rs = null;
		SqlRowList rs1 = null;
		Integer acid = 0;
		boolean sourcetype = baseDao.isDBSetting("autoCreateApBill");
		Object type = maps.get(0).get("ad_sourcetype");
		String tocaller = null;
		String log = null;
		if (sourcetype) {
			if ("APBILL".equals(type)) {
				tocaller = "APCheck!ToBillOutAP";
			} else if ("PRODINOUT".equals(type)) {
				tocaller = "APCheck!ToBillOutAP!PD";
			}
		} else {
			tocaller = "APCheck!ToAPBill";
		}
		for (Map<Object, Object> map : maps) {
			int adid = Integer.parseInt(map.get("ad_id").toString());
			double tqty = Double.parseDouble(map.get("ad_tqty").toString());
			double qty = Double.parseDouble(map.get("ad_qty").toString());
			if (qty < 0 && tqty > 0) {
				BaseUtil.showError("对账数量为负数的，本次转数量不能为正数！");
			}
			if (qty > 0 && tqty < 0) {
				BaseUtil.showError("对账量为正数的，本次转数量不能为负数！");
			}
			if (tqty == 0) {
				BaseUtil.showError("本次转数量不能为0！");
			}
			rs = baseDao.queryForRowSet("SELECT ad_sourcecode,ad_sourcedetno,ad_pdid,ad_detno FROM apcheckdetail WHERE ad_id=" + adid);
			if (rs.next()) {
				if (sourcetype) {
					if ("APBILL".equals(type)) {
						y = baseDao.getFieldDataByCondition(
								"BillOutAPDetail",
								"sum(nvl(ard_nowqty,0))",
								"ard_ordercode='" + rs.getObject("ad_sourcecode") + "' and ard_orderdetno="
										+ rs.getObject("ad_sourcedetno"));
						sourceqty = baseDao.getFieldDataByCondition("APBillDetail", "nvl(abd_qty,0)",
								"abd_code='" + rs.getObject("ad_sourcecode") + "' and abd_detno=" + rs.getObject("ad_sourcedetno"));
					} else if ("PRODINOUT".equals(type)) {
						rs1 = baseDao.queryForRowSet("SELECT abd_code,abd_detno,abd_qty FROM APBillDetail WHERE ABD_PDID="
								+ rs.getGeneralInt("ad_pdid"));
						if (rs1.next()) {
							y = baseDao.getFieldDataByCondition("BillOutAPDetail", "sum(nvl(ard_nowqty,0))",
									"ard_ordercode='" + rs1.getObject("abd_code") + "' and ard_orderdetno=" + rs1.getObject("abd_detno"));
							sourceqty = rs1.getObject("abd_qty");
						} else {
							sb.append("[出入库单对应的发票不存在],行号:").append(rs.getInt("ad_detno")).append("<hr/>");
						}
					}
				} else {
					y = baseDao.getFieldDataByCondition("APBillDetail", "sum(nvl(abd_qty,0))", "ABD_PDID=" + rs.getGeneralInt("ad_pdid"));
					sourceqty = baseDao.getFieldDataByCondition("ProdIODetail", "nvl(pd_inqty,0)-nvl(pd_outqty,0)",
							"pd_id=" + rs.getGeneralInt("ad_pdid"));
				}
			}
			y = y == null ? 0 : y;
			sourceqty = sourceqty == null ? 0 : sourceqty;
			if (Math.abs(Double.parseDouble(y.toString()) + tqty) > Math.abs(Double.parseDouble(sourceqty.toString()))) {
				sb.append("[本次数量填写超出可转数量],行号:").append(rs.getInt("ad_detno")).append(",数量:").append(sourceqty).append(",已转数:").append(y)
						.append(",本次数:").append(tqty).append("<hr/>");
			}
		}
		if (sb.length() > 0) {
			BaseUtil.showError(sb.toString());
		}
		if (maps.size() > 0) {
			acid = baseDao.getFieldValue("apcheckdetail", "ad_acid", "ad_id=" + maps.get(0).get("ad_id"), Integer.class);
			int id = 0;
			Key key = transferRepository.transfer(tocaller, acid);
			id = key.getId();
			transferRepository.transfer(tocaller, maps, key);
			if (sourcetype) {
				Timestamp bi_date = baseDao.getJdbcTemplate().queryForObject("select bi_date from BillOutAP where bi_id=?",
						Timestamp.class, id);
				rs = baseDao
						.queryForRowSet(
								"SELECT ab_paymentcode,bi_vendcode FROM (SELECT AB_PAYMENTCODE,bi_vendcode FROM BILLOUTAP left join BILLOUTAPDETAIL on bi_id=ard_biid LEFT JOIN APBILL ON ARD_ORDERCODE=AB_CODE WHERE ARD_BIID=? AND NVL(AB_PAYMENTCODE,' ')<>' ' ORDER BY ARD_DETNO) WHERE ROWNUM<2",
								id);
				if (rs.next()) {
					String res = baseDao.callProcedure("SP_GETPAYDATE_VEND",
							new Object[] { bi_date, rs.getObject("ab_paymentcode"), 0, rs.getObject("bi_vendcode") });
					baseDao.updateByCondition("BILLOUTAP", "bi_paydate='" + res + "'", " bi_id=" + id);
				}
			}
			if (sourcetype) {
				baseDao.execute("update BillOutAPDetail set ard_nowbalance=round(nvl(ard_nowqty,0)*round(nvl(ard_nowprice,0),8),2) where ard_biid="
						+ id);
				baseDao.execute("update BillOutAPDetail set ard_taxamount=round(ard_nowbalance*nvl(ard_taxrate,0)/(100+nvl(ard_taxrate,0)),2) where ard_biid="
						+ id);
				baseDao.execute("update BillOutAP set bi_amount=round(nvl((select sum(round(ard_nowbalance,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) where bi_id="
						+ id);
				baseDao.execute("update BillOutAP set bi_taxamount=round(nvl((select sum(round(ard_taxamount,2)) from BillOutAPDetail where ard_biid=bi_id),0),2) + nvl(bi_taxdiffer,0) where bi_id="
						+ id);
				baseDao.execute("update BillOutAP set (bi_departmentcode,bi_department)=(select ab_departmentcode,ab_departmentname from BillOutAPDetail left join apbill on ard_ordercode=ab_code where ard_biid=bi_id and ard_detno=1 and nvl(ab_departmentname,' ')<>' ') where bi_id="
						+ id);
				baseDao.execute("delete from BillOutAPDetail where nvl(ard_ordercode,' ')=' ' and nvl(ard_orderdetno,0)=0 and ard_biid="
						+ id);
				baseDao.execute("update APBILLDETAIL SET abd_yqty=(SELECT NVL(SUM(ard_nowqty),0) FROM BillOutAPDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO and nvl(ARD_ADID,0)=0)"
						+ " WHERE EXISTS (SELECT 1 FROM BillOutAPDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO and nvl(ARD_ADID,0)=0 AND ard_biid="
						+ id + ")");
				handlerService.handler(caller, "turn", "after", new Object[] { id });
				log = "发票号:" + "<a href=\"javascript:openUrl('jsps/fa/arp/billOutAP.jsp?formCondition=bi_idIS" + id
						+ "&gridCondition=ard_biidIS" + id + "&whoami=BillOutAP')\">" + key.getCode() + "</a>&nbsp;";
			} else {
				baseDao.execute("update apbill set ab_vendid=(select ve_id from vendor where ab_vendcode=ve_code) where ab_id=" + id);
				baseDao.execute("update apbill set ab_paymentid=(select pa_id from payments where pa_code=ab_paymentcode and pa_class='收款方式') where ab_id="
						+ id);
				baseDao.execute("update apbilldetail set abd_code=(select ab_code from arbill where abd_abid=ab_id) WHERE abd_abid=" + id);
				baseDao.execute("UPDATE apbilldetail SET abd_apamount=round(abd_thisvoprice*abd_qty,2) WHERE abd_abid=" + id);
				baseDao.execute("UPDATE apbilldetail SET abd_noapamount=round(abd_qty*abd_thisvoprice/(1+abd_taxrate/100),2) WHERE abd_abid="
						+ id);
				baseDao.execute("update APBILLDETAIL SET abd_yqty=(SELECT NVL(SUM(ard_nowqty),0) FROM BillOutAPDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO and nvl(ard_adid,0)=0)"
						+ " WHERE abd_abid="
						+ id
						+ " and EXISTS (SELECT 1 FROM BillOutAPDetail WHERE ard_ordercode=ABD_CODE AND ard_orderdetno=ABD_DETNO)");
				baseDao.execute("UPDATE apbilldetail SET abd_taxamount=round((abd_qty*abd_thisvoprice*abd_taxrate/100)/(1+abd_taxrate/100),2) WHERE abd_abid="
						+ id);
				baseDao.execute("update apbill set ab_taxsum=(select sum(round(((abd_thisvoprice*abd_qty*abd_taxrate/100)/(1+abd_taxrate/100)),2)) from apbilldetail where abd_abid="
						+ id + ")+nvl(ab_differ,0) where ab_id=" + id);
				baseDao.execute("update apbill set (ab_departmentcode,ab_departmentname)=(select pi_departmentcode,pi_departmentname from APBILLDetail left join ProdInOut on abd_pdinoutno=pi_inoutno where abd_abid=ab_id and abd_detno=1 and nvl(pi_departmentname,' ')<>' ') where ab_id="
						+ id);
				baseDao.execute("delete from apbilldetail where abd_abid=" + id
						+ " and exists (select * from Prodiodetail where abd_pdid=pd_id and pd_piclass in ('不良品入库单','不良品出库单'))");
				baseDao.execute("update ProdIODetail SET pd_showinvoqty=(SELECT NVL(SUM(abd_qty),0) FROM APBillDetail WHERE abd_sourcedetailid=pd_id AND abd_sourcekind='PRODIODETAIL' and nvl(abd_adid,0)=0)"
						+ " WHERE EXISTS (SELECT 1 FROM APBillDetail WHERE abd_sourcedetailid=pd_id AND abd_sourcekind='PRODIODETAIL' AND abd_abid="
						+ id + ")");
				baseDao.execute("update estimatedetail SET esd_showinvoqty=(SELECT NVL(SUM(abd_qty),0) FROM APBillDetail WHERE abd_sourcedetailid=esd_id AND abd_sourcekind='ESTIMATE' and nvl(abd_adid,0)=0)"
						+ " WHERE EXISTS (SELECT 1 FROM APBillDetail WHERE abd_sourcedetailid=esd_id AND abd_sourcekind='ESTIMATE' AND abd_abid="
						+ id + ")");
				handlerService.handler(caller, "turn", "after", new Object[] { id });
				log = "发票号:" + "<a href=\"javascript:openUrl('jsps/fa/ars/apbill.jsp?formCondition=ab_idIS" + id
						+ "&gridCondition=abd_abidIS" + id + "&whoami=APBill!CWIM')\">" + key.getCode() + "</a>&nbsp;";
			}
		}
		// 更新主表开票状态
		apCheckDao.updateBillStatus(acid);
		return "转入成功<hr>" + log;
	}

	final static String INSERT_PAYPLEASEDETAILDET = "insert into PAYPLEASEDETAILDET(ppdd_id, "
			+ "ppdd_ppdid, ppdd_detno, ppdd_billcode, ppdd_currency, ppdd_paymethod,"
			+ "ppdd_billdate, ppdd_planpaydate, ppdd_account, ppdd_billamount, ppdd_thisapplyamount,"
			+ " ppdd_turnamount, ppdd_paymethodid,ppdd_ppid) " + "values (PAYPLEASEDETAILDET_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?,?,0,?,?)";

	@Override
	public JSONObject turnPayPlease(int id, String caller) {
		Object accode = baseDao.getFieldDataByCondition("APCheck", "ac_code", "ac_id=" + id);
		accode = baseDao.getFieldDataByCondition("PayPlease", "pp_code", "pp_sourcecode='" + accode + "' and pp_sourcetype='应付对账单'");
		if (accode != null && !accode.equals("")) {
			baseDao.execute("update APCheck set AC_TURNSTATUS='已转付款申请' where ac_id=" + id);
			BaseUtil.showError("该对账单已经转入过付款申请单[" + accode + "]！");
		} else {
			int ppid = 0;
			int ppdid = 0;
			Employee employee = SystemSession.getUser();
			String code = null;
			int count = baseDao
					.getCount("select count(1) from APCHECK left join APCHECKDETAIL on AC_ID=AD_ACID full join PRODIODETAIL on NVL(AD_PDID,0)=PD_ID where ac_id="
							+ id + " and nvl(PD_PICLASS,' ') not in ('不良品入库单','不良品出库单')");
			if (count > 0) {
				ppid = baseDao.getSeqId("PAYPLEASE_SEQ");
				ppdid = baseDao.getSeqId("PAYPLEASEDETAIL_SEQ");
				code = baseDao.sGetMaxNumber("PayPlease", 2);
				baseDao.execute("INSERT INTO PayPlease(pp_id, pp_code, pp_date, pp_applydept, pp_type, pp_apply, pp_status,"
						+ "pp_total, pp_paystatus, pp_statuscode, pp_paystatuscode, pp_applyid, pp_sourcecode," + "pp_sourcetype)"
						+ " select " + ppid + ",'" + code + "',sysdate,'" + employee.getEm_depart() + "','应付款','" + employee.getEm_name()
						+ "','" + BaseUtil.getLocalMessage("ENTERING") + "', ac_checkamount, '" + BaseUtil.getLocalMessage("UNPAYMENT")
						+ "', 'ENTERING'," + "'UNPAYMENT'," + employee.getEm_id() + ",ac_code,'应付对账单'" + " from apcheck where ac_id=" + id);
				baseDao.execute("INSERT INTO PayPleaseDetail(ppd_id, ppd_ppid, ppd_vendid, ppd_detno, ppd_vendcode, ppd_vendname, ppd_paymethod,"
						+ "ppd_bankname, ppd_bankaccount, ppd_currency, ppd_bankman, ppd_applyamount, ppd_auditamount,ppd_startdate,ppd_overdate,"
						+ "ppd_account,ppd_paymethodcode,ppd_paymethodid,ppd_acid)"
						+ " select "
						+ ppdid
						+ ","
						+ ppid
						+ ",ve_id,1,ac_vendcode,ac_vendname,ve_payment,"
						+ "ve_bank,ve_bankaccount,ac_currency,ve_bankman,ac_checkamount,ac_checkamount,ac_fromdate,ac_todate,"
						+ "0,ve_paymentcode,ve_paymentid,ac_id from apcheck left join vendor on ac_vendcode=ve_code where ac_id=" + id);
			}
			if (ppid != 0 && ppdid != 0) {
				int detno = 1;
				List<Map<Object, Object>> params = new ArrayList<Map<Object, Object>>();
				SqlRowList rs = baseDao
						.queryForRowSet(
								"select ad_sourcecode,ad_sourcetype,sum(nvl(ad_amount,0)) ad_amount from APCHECKDETAIL where AD_ACID=? group by ad_sourcecode,ad_sourcetype",
								id);
				while (rs.next()) {
					Map<Object, Object> map = new HashMap<Object, Object>();
					map.put("adamount", rs.getGeneralDouble("ad_amount"));
					String sourcetype = rs.getGeneralString("ad_sourcetype");
					if ("APBILL".equals(sourcetype)) {
						map.put("abcode", rs.getGeneralString("ad_sourcecode"));
					} else if ("PRODINOUT".equals(sourcetype)) {
						SqlRowList abs = baseDao
								.queryForRowSet(
										"select distinct abd_code from APBILLDETAIL,PRODINOUT where ABD_PDINOUTNO=PI_INOUTNO AND ABD_PDINOUTNO=? AND PI_CLASS NOT IN ('不良品入库单','不良品出库单')",
										rs.getString("ad_sourcecode"));
						while (abs.next()) {
							map.put("abcode", abs.getGeneralString("abd_code"));
						}
					}
					params.add(map);
				}
				if (params != null && !"".equals(params)) {
					for (Map<Object, Object> m : params) {
						SqlRowList abs = baseDao.queryForRowSet("select * from APBILL where ab_code=?", m.get("abcode"));
						if (abs.next()) {
							baseDao.execute(
									INSERT_PAYPLEASEDETAILDET,
									new Object[] { ppdid, detno++, abs.getObject("ab_code"), abs.getObject("ab_currency"),
											abs.getObject("ab_payments"), abs.getObject("ab_date"), abs.getObject("ab_planpaydate"),
											abs.getObject("ab_payamount"), abs.getObject("ab_apamount"), m.get("adamount"),
											abs.getObject("ab_paymentid"), ppid });
						}
					}
				}
				// 生成日志
				baseDao.logger.others("转付款申请单", "转入成功", "APCheck", "ac_id", id);
				JSONObject j = new JSONObject();
				j.put("pp_id", ppid);
				j.put("pp_code", code);
				baseDao.execute("update APCheck set AC_TURNSTATUS='已转付款申请' where ac_id=" + id);
				return j;
			}
		}
		return null;
	}

	@Override
	public void submitAPCheckConfirm(int id) {
		handlerService.handler("APCheck!Confirm", "commit", "before", new Object[] { id });
		baseDao.execute("update APCheck set ac_confirmstatus='已提交' where ac_id=" + id);
		baseDao.logger.others("提交(确认)", "提交成功", "APCheck", "ac_id", id);
		handlerService.handler("APCheck!Confirm", "commit", "after", new Object[] { id });
	}

	@Override
	public void resSubmitAPCheckConfirm(int id) {
		handlerService.handler("APCheck!Confirm", "resCommit", "before", new Object[] { id });
		baseDao.execute("update APCheck set ac_confirmstatus='未确认' where ac_id=" + id);
		baseDao.logger.others("反提交(确认)", "反提交成功", "APCheck", "ac_id", id);
		handlerService.handler("APCheck!Confirm", "resCommit", "after", new Object[] { id });
	}
}

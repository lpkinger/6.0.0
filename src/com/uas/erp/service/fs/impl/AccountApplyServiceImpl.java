package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.model.ScheduleTask;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fa.AccountRegisterBankService;
import com.uas.erp.service.fs.AccountApplyService;

@Service("accountApplyService")
public class AccountApplyServiceImpl implements AccountApplyService {

	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;
	@Autowired
	private AccountRegisterBankService accountRegisterBankService;
	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void saveAccountApply(String formStore, String param1, String param2, String param3, String param4, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		handlerService.handler(caller, "save", "before", new Object[] { store });

		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "AccountApply"));
		// 保存AccountApplySa
		if (param1 != null && !"".equals(param1)) {
			for (Map<Object, Object> m : grid) {
				m.put("aas_id", baseDao.getSeqId("ACCOUNTAPPLYSA_SEQ"));
			}
		}
		// 保存AccountApplyInv
		if (param2 != null && !"".equals(param2)) {
			for (Map<Object, Object> m : grid2) {
				m.put("aai_id", baseDao.getSeqId("ACCOUNTAPPLYINV_SEQ"));
			}
		}
		// 保存AccountApplyBill
		if (param3 != null && !"".equals(param3)) {
			for (Map<Object, Object> m : grid3) {
				m.put("ab_id", baseDao.getSeqId("ACCOUNTAPPLYBILL_SEQ"));
			}
		}
		// 保存REIMBURSEMENTPLAN
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				m.put("rp_id", baseDao.getSeqId("REIMBURSEMENTPLAN_SEQ"));
				m.put("rp_code", baseDao.sGetMaxNumber("REIMBURSEMENTPLAN", 2));
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid, "AccountApplySa"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid2, "AccountApplyInv"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid3, "AccountApplyBill"));
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(grid4, "ReimbursementPlan"));
		getTotal(caller, store.get("aa_id"));

		baseDao.logger.save(caller, "aa_id", store.get("aa_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateAccountApply(String formStore, String param1, String param2, String param3, String param4, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		handlerService.handler(caller, "save", "before", new Object[] { store });

		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "AccountApply", "aa_id"));
		List<String> gridSql = new ArrayList<String>();
		// 更新ACCOUNTAPPLYSA
		if (param1 != null && !"".equals(param1)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "ACCOUNTAPPLYSA", "aas_id"));
		}
		// 更新ACCOUNTAPPLYINV
		if (param2 != null && !"".equals(param2)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid2, "ACCOUNTAPPLYINV", "aai_id"));
		}
		// 更新AccountApplyBill
		if (param3 != null && !"".equals(param3)) {
			gridSql.addAll(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid3, "AccountApplyBill", "ab_id"));
		}
		baseDao.execute(gridSql);
		// 更新ReimbursementPlan
		if (param4 != null && !"".equals(param4)) {
			for (Map<Object, Object> m : grid4) {
				// m.put("rp_id", baseDao.getSeqId("REIMBURSEMENTPLAN_SEQ"));
				m.put("rp_code", baseDao.sGetMaxNumber("REIMBURSEMENTPLAN", 2));
			}
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid4, "ReimbursementPlan", "rp_id"));
		}
		getTotal(caller, store.get("aa_id"));

		// 记录操作
		baseDao.logger.update(caller, "aa_id", store.get("aa_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	void getTotal(String caller, Object aa_id) {
		if ("AccountApply!HX".equals(caller)) {
			baseDao.execute("update ACCOUNTAPPLY set aa_transferamount=nvl((select sum(ab_billamount) from AccountApplyBill where ab_aaid=aa_id),0) where aa_id="
					+ aa_id);
			baseDao.execute("update ACCOUNTAPPLY set aa_wantamount=nvl(aa_transferamount,0) where aa_id=" + aa_id);
		}
		baseDao.execute("update ACCOUNTAPPLY set aa_billamount=nvl((select sum(aai_amount) from ACCOUNTAPPLYINV where aai_aaid=aa_id),0) where aa_id="
				+ aa_id);
		baseDao.execute("update ACCOUNTAPPLY set aa_saamount=nvl((select sum(aas_amount) from ACCOUNTAPPLYSA where aas_aaid=aa_id),0) where aa_id="
				+ aa_id);
		baseDao.execute("update ACCOUNTAPPLY set aa_leftamount=nvl(AA_DUEAMOUNT,0) where aa_id=" + aa_id);
		baseDao.execute("update ACCOUNTAPPLY set aa_lendrate=round(nvl(aa_wantamount,0)/nvl(aa_transferamount,0)*100,2) where aa_id="
				+ aa_id + " and nvl(aa_transferamount,0)<>0");
		baseDao.execute("update ACCOUNTAPPLY set aa_hand=round(nvl(aa_transferamount,0)*nvl(aa_handrate,0)/100,2) where aa_id=" + aa_id);
	}

	@Override
	public void deleteAccountApply(int aa_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { aa_id });

		baseDao.delCheck("AccountApply", aa_id);

		// 删除主表内容
		baseDao.deleteById("AccountApply", "aa_id", aa_id);
		// 删除AccountApplySa
		baseDao.deleteById("AccountApplySa", "aas_aaid", aa_id);
		// 删除AccountApplyInv
		baseDao.deleteById("AccountApplyInv", "aai_aaid", aa_id);
		// 删除AccountApplyBill
		baseDao.deleteById("AccountApplyBill", "ab_aaid", aa_id);
		// 删除reimbursementplan
		baseDao.deleteById("reimbursementplan", "rp_aaid", aa_id);
		baseDao.logger.delete(caller, "aa_id", aa_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { aa_id });
	}

	@Override
	public void submitAccountApply(int aa_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountApply", new String[] { "aa_statuscode", "aa_interestpaymethod" },
				"aa_id=" + aa_id);
		StateAssert.submitOnlyEntering(status[0]);
		getTotal(caller, aa_id);

		String dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(AMOUNT) from (select ROUND(NVL(CQ_QUOTA,0)-NVL(CQ_YQUOTA,0),2) AMOUNT from ACCOUNTAPPLY,CUSTOMERQUOTA where AA_CACODE=CQ_CODE "
								+ "and AA_CLASS='保理额度申请出账' and nvl(aa_dueamount,0)>ROUND(NVL(CQ_QUOTA,0)-NVL(CQ_YQUOTA,0),2) and AA_ID=?)",
						String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError("保理首付款金额不能超过额度申请单的剩余额度" + dets + "！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(AMOUNT) from (select ROUND(NVL(ca_factorquota,0)-NVL(ca_factorquota,0),2) AMOUNT from ACCOUNTAPPLY,CUSTOMERQUOTAAPPLY where AA_CACODE=CA_CODE "
								+ "and AA_CLASS='核心企业额度申请出账' and nvl(aa_dueamount,0)>ROUND(NVL(ca_factorquota,0)-NVL(CA_YQUOTA,0),2) and AA_ID=?)",
						String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError("保理首付款金额不能超过核心额度申请单的剩余额度" + dets + "！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(aa_code) from AccountApply where nvl(aa_saamount,0)<nvl(aa_billamount,0) and aa_id=?", String.class,
				aa_id);
		if (dets != null) {
			BaseUtil.showError("合同总金额不能小于发票总金额！");
		}
		if ("AccountApply!HX".equals(caller)) {
			int count = baseDao.getCountByCondition("AccountApplyBill", "ab_aaid = " + aa_id);
			if (count == 0) {
				BaseUtil.showError("票据详情不能为空！");
			}
		} else {
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(cq_lendrate) from AccountApply,CustomerQuota where aa_cacode=cq_code and nvl(aa_lendrate,0)>nvl(cq_lendrate,0) and aa_id=?",
							String.class, aa_id);
			if (dets != null) {
				BaseUtil.showError("融资比例不能大于额度申请中融资比例！" + dets);
			}
			dets = baseDao
					.getJdbcTemplate()
					.queryForObject(
							"select wm_concat(cq_lendrate) from AccountApply,CustomerQuota where aa_cacode=cq_code and aa_dueamount > (select sum(nvl(aas_amount,0) - nvl(sa_usedamount,0)) from ACCOUNTAPPLYSA,FSSale where aas_sacode=code and aas_aaid = aa_id)*NVL(cq_lendrate,0)/100 and aa_id = ?",
							String.class, aa_id);
			if (dets != null) {
				BaseUtil.showError("保理首付款不能超过合同剩余金额的" + dets + "%！");
			}
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(aa_code) from AccountApply where nvl(aa_transferamount,0)>nvl(aa_saamount,0) and aa_id=?", String.class,
				aa_id);
		if (dets != null) {
			BaseUtil.showError("保理转让款不能大于合同总金额！" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(aa_code) from AccountApply where nvl(aa_wantamount,0)>nvl(aa_transferamount,0) and aa_id=?",
				String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError("拟融资金额不能大于保理转让款！" + dets);
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(aa_code) from AccountApply where nvl(aa_dueamount,0)>nvl(aa_wantamount,0) and aa_id=?", String.class,
				aa_id);
		if (dets != null) {
			BaseUtil.showError("保理首付款不能大于拟融资金额！" + dets);
		}
		// dets = baseDao.getJdbcTemplate().queryForObject(
		// "select wm_concat(aa_code) from AccountApply where nvl(aa_billamount,0)<nvl(aa_transferamount,0) and aa_id=?",
		// String.class, aa_id);
		// if (dets != null) {
		// BaseUtil.showError("发票总金额不能小于保理转让款！");
		// }
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(nvl(cq_singlelimit,0)) from AccountApply,CustomerQuota where aa_cacode=cq_code and aa_maturitydate-aa_loandate>nvl(cq_singlelimit,0) and aa_id=?",
						String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError("应收账款到期日-计划放款日期>单笔最长融资期限！单笔最长融资期限：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select wm_concat(cq_handrate) from AccountApply,CustomerQuota where aa_cacode=cq_code and nvl(aa_handrate,0)<nvl(cq_handrate,0) and aa_id=?",
						String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError("手续费率不能小于额度申请中手续费率" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"SELECT WM_CONCAT('行['||AAS_DETNO||']合同编号['||AAS_SACODE||']') FROM ACCOUNTAPPLYSA WHERE AAS_AAID=? AND AAS_SACODE in (select aas_sacode from ACCOUNTAPPLYSA where AAS_AAID=? group by aas_sacode having count(*)>1)",
						String.class, aa_id, aa_id);
		if (dets != null) {
			BaseUtil.showError("合同编号重复：" + dets);
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select WM_CONCAT('行['||aas_detno||']合同编号['||aas_sacode||']') from AccountApply,ACCOUNTAPPLYSA,FSSale where aas_aaid = aa_id and aas_sacode=code "
								+ "and NVL(SA_USEDAMOUNT,0) + nvl(aa_dueamount,0)/nvl(aa_wantamount,0)*nvl(aas_amount,0) > nvl(sa_total,0) and nvl(aa_wantamount,0)<>0 and aa_id = ?",
						String.class, aa_id);
		if (dets != null) {
			BaseUtil.showError(dets + ",保理首付款超过合同剩余金额允许融资比例！");
		}
		dets = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"SELECT WM_CONCAT('行['||aai_detno||']合同编号['||aai_invoiceno||']') FROM ACCOUNTAPPLYINV WHERE aai_aaid=? AND aai_invoiceno in (select aai_invoiceno from ACCOUNTAPPLYINV where aai_aaid=? group by aai_invoiceno having count(*)>1)",
						String.class, aa_id, aa_id);
		if (dets != null) {
			BaseUtil.showError("发票号码重复：" + dets);
		}
		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { aa_id });
		// 执行提交操作
		baseDao.submit("AccountApply", "aa_id=" + aa_id, "aa_status", "aa_statuscode");

		// 更新合同已使用合同金额
		baseDao.execute(
				"update FSSale set SA_USEDAMOUNT = NVL(SA_USEDAMOUNT,0) + (select nvl(aa_dueamount,0)/nvl(aa_wantamount,0)*nvl(aas_amount,0) from AccountApply,ACCOUNTAPPLYSA where aas_aaid = aa_id and nvl(aa_wantamount,0)<>0 and aa_id = ? and aas_sacode=code) where code in (select aas_sacode from ACCOUNTAPPLYSA where aas_aaid = ?)",
				aa_id, aa_id);

		// 记录操作
		baseDao.logger.submit(caller, "aa_id", aa_id);

		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { aa_id });
	}

	@Override
	public void resSubmitAccountApply(int aa_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("AccountApply", "aa_statuscode", "aa_id=" + aa_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { aa_id });
		// 执行反提交操作
		baseDao.resOperate("AccountApply", "aa_id=" + aa_id, "aa_status", "aa_statuscode");

		// 更新合同已使用合同金额
		baseDao.execute(
				"update FSSale set SA_USEDAMOUNT = NVL(SA_USEDAMOUNT,0) - (select nvl(aa_dueamount,0)/nvl(aa_wantamount,0)*nvl(aas_amount,0) from AccountApply,ACCOUNTAPPLYSA where aas_aaid = aa_id and nvl(aa_wantamount,0)<>0 and aa_id = ? and aas_sacode=code) where code in (select aas_sacode from ACCOUNTAPPLYSA where aas_aaid = ?)",
				aa_id, aa_id);

		// 记录操作
		baseDao.logger.resSubmit(caller, "aa_id", aa_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { aa_id });
	}

	@Override
	@Transactional
	public void auditAccountApply(int aa_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("AccountApply",
				"aa_statuscode,aa_cacode,aa_custcode,aa_mfcustcode,aa_dueamount,aa_aaid,aa_class", "aa_id=" + aa_id);
		StateAssert.auditOnlyCommited(status[0]);
		getTotal(caller, aa_id);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { aa_id });
		Object[] cust = baseDao.getFieldsDataByCondition("CustomerQuota inner join FSMFCUSTINFO on cq_id = MF_CQID", new String[] {
				"mf_sourcecode", "mf_custname", "cq_finid" }, "cq_code = '" + status[1] + "' and mf_custcode = '" + status[3] + "'");
		if (null != cust) {
			try {
				if (null != cust[2] && null != status[5]) {
					Object uu = baseDao.getFieldDataByCondition("CustomerInfor", "cu_enuu", "cu_code = '" + status[2]
							+ "' and nvl(cu_b2benable,0)<>0");
					if (null != uu) {
						Master master = SystemSession.getUser().getCurrentMaster();
						Map<String, String> params = new HashMap<String, String>();
						Map<String, Object> sellerQuota = new HashMap<String, Object>();
						sellerQuota.put("faid", cust[2]);
						sellerQuota.put("cq_custname", cust[1]);
						sellerQuota.put("cq_dueamount", status[4]);
						params.put("sellerQuota", FlexJsonUtil.toJson(sellerQuota));
						params.put("aaid", String.valueOf(status[5]));
						params.put("iscloseoff", "未结清");
						Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite() + "/sellerquota/erp/updateQuota?access_id="
								+ master.getMa_uu(), params, true, master.getMa_accesssecret());
						if (response.getStatusCode() != HttpStatus.OK.value()) {
							throw new Exception("连接平台失败," + response.getStatusCode());
						}
					}
				} else {
					SqlRowList rs = baseDao.queryForRowSet(
							"select cu_webserver,cu_whichsystem,cu_secret FROM CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0",
							status[2]);
					if (rs.next()) {
						String web = rs.getGeneralString("cu_webserver");
						String whichsys = rs.getGeneralString("cu_whichsystem");
						String secret = rs.getGeneralString("cu_secret");

						if (!StringUtil.hasText(web) || !StringUtil.hasText(whichsys)) {
							BaseUtil.showError("客户资料的网址或账套不明，无法正常取数！");
						}

						if (!StringUtil.hasText(secret)) {
							BaseUtil.showError("密钥为空，不能审批系统客户额度申请！");
						}
						Map<String, String> params = new HashMap<String, String>();
						params.put("cqcode", String.valueOf(status[1]));
						params.put("custcode", String.valueOf(cust[0]));
						params.put("custname", String.valueOf(cust[1]));
						params.put("amount", String.valueOf(status[4]));
						Response response = HttpUtil.sendPostRequest(web + "/openapi/factoring/AccountApply.action?master=" + whichsys,
								params, true, secret);
						if (response.getStatusCode() != HttpStatus.OK.value()) {
							throw new Exception("连接客户账套失败," + response.getStatusCode());
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("错误：" + e.getMessage());
			}
		}

		baseDao.audit("AccountApply", "aa_id=" + aa_id, "aa_status", "aa_statuscode", "aa_auditdate", "aa_auditman");
		if ("保理额度申请出账".equals(status[6])) {
			baseDao.execute("update CUSTOMERQUOTA set CQ_YQUOTA=nvl(CQ_YQUOTA,0)+" + status[4] + " where cq_code='" + status[1] + "'");
		} else if ("核心企业额度申请出账".equals(status[6])) {
			baseDao.execute("update CUSTOMERQUOTAAPPLY set CA_YQUOTA=nvl(CA_YQUOTA,0)+" + status[4] + " where ca_code='" + status[1] + "'");
		}
		// 转银行登记
		turnBankRegister(caller, aa_id);
		// // 产生还款计划
		// baseDao.procedure("FS_CREATEREIMBURSEMENTPLAN", new Object[] { aa_id
		// });
		// // 如果系统日期大于还款计划还款还款日期，插入利息单
		// int count =
		// baseDao.getCount("select count(1) from REIMBURSEMENTPLAN,ACCOUNTAPPLY where AA_CODE=RP_AACODE and aa_id="
		// + aa_id
		// + " and TRUNC(RP_BACKDATE)<TRUNC(sysdate)");
		// if (count > 0) {
		// baseDao.execute("insert into FSINTEREST(IN_ID,IN_CODE,IN_AACODE,IN_CURRENCY,IN_INTEREST,IN_ISCLOSEOFF,IN_TRUSTER,IN_APPLYDATE,IN_YAMOUNT,IN_REMARK,IN_DATE) "
		// +
		// "select FSINTEREST_SEQ.NEXTVAL,'IN000'||rp_id,RP_AACODE,RP_CURRENCY,RP_INTEREST,'否',RP_TRUSTER,RP_APPLYDATE,0,aa_REMARK,RP_BACKDATE "
		// +
		// "from REIMBURSEMENTPLAN,ACCOUNTAPPLY where AA_CODE=RP_AACODE and aa_id="
		// + aa_id
		// + " and TRUNC(RP_BACKDATE)<TRUNC(sysdate)");
		// baseDao.execute("update reimbursementplan set rp_iscarryout='已执行' where TRUNC(RP_BACKDATE)<TRUNC(sysdate) and RP_AACODE=(select aa_code from accountapply where aa_id="
		// + aa_id + ")");
		// }
		// // 更新利息和还款计划执行状态
		// baseDao.execute("update ACCOUNTAPPLY set AA_INTEREST=(TRUNC(sysdate)-TRUNC(AA_LOANDATE))*ROUND(AA_LEFTAMOUNT*AA_INTERESTRATE/100/365,2) where aa_id="
		// + aa_id);
		// baseDao.execute("update ACCOUNTAPPLY set AA_INTERESTPAY=NVL(AA_INTEREST,0)-nvl((select SUM(IN_INTEREST) from FSINTEREST where IN_AACODE=AA_CODE),0) where aa_id="
		// + aa_id);
		// // 如果到期日期小于系统日期，生成逾期单
		// count =
		// baseDao.getCount("select count(1) from ACCOUNTAPPLY where aa_id=" +
		// aa_id + " and TRUNC(aa_maturitydate)<TRUNC(sysdate)");
		// if (count > 0) {
		// baseDao.execute("insert into FSOVERDUE(OD_ID,OD_CODE,OD_AACODE,OD_CURRENCY,OD_ODAMOUNT,OD_PENALTYRATE,OD_ODDAYS,OD_ODINTEREST,OD_BACKAMOUNT,OD_ISCLOSEOFF,"
		// +
		// "od_remark,od_truster,od_applydate,od_custcode,od_custname) select FSOVERDUE_SEQ.NEXTVAL,'"
		// + baseDao.sGetMaxNumber("FsOverdue", 2)
		// + "',aa_code,AA_CURRENCY,AA_LEFTAMOUNT,AA_PENALTYRATE,"
		// +
		// "TRUNC(sysdate)-TRUNC(aa_maturitydate),ROUND(NVL(AA_LEFTAMOUNT,0)*NVL(AA_PENALTYRATE,0)/100/365,2)*(TRUNC(SYSDATE)-TRUNC(AA_MATURITYDATE)),0,'否',aa_remark,aa_truster,aa_applydate,aa_custcode,aa_custname from ACCOUNTAPPLY where aa_id="
		// + aa_id);
		// baseDao.execute("update accountapply set aa_isoverdue='是' where aa_id="
		// + aa_id);
		// }
		// 更新保理进度
		baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '放款',FS_STATUSDATE = sysdate", "FS_CQCODE = '" + status[1]
				+ "' and FS_STATUSDATE is null");
		Master master = SystemSession.getUser().getCurrentMaster();
		Master parentMaster = null;
		if (master != null && master.getMa_pid() != null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (null != parentMaster) {
			baseDao.execute("update " + parentMaster.getMa_user()
					+ ".FINBUSINAPPLY set FS_STATUS = '放款',FS_STATUSDATE=sysdate where FS_CQCODE='" + status[1]
					+ "' and FS_STATUSDATE is null");
		}
		// 记录操作
		baseDao.logger.audit(caller, "aa_id", aa_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { aa_id });
	}

	@Override
	@Transactional
	public void resAuditAccountApply(int aa_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("AccountApply", new String[] { "aa_statuscode", "aa_code", "aa_cacode",
				"nvl(aa_dueamount,0)" }, "aa_id=" + aa_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("AccountApply", aa_id);
		String dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(ra_code) from ReimbursementApply where ra_aacode=?",
				String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在还款申请[" + dets + "]，不允许反审核！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(in_code) from Fsinterest where in_aacode=? and nvl(in_yamount,0)>0", String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("利息单[" + dets + "]已转银行登记，不允许反审核！");
		}
		dets = baseDao.getJdbcTemplate().queryForObject("select wm_concat(od_code) from Fsoverdue where od_aacode=?", String.class,
				status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在逾期单[" + dets + "]，不允许反审核！");
		}
		// 删除银行登记
		Object ar = baseDao.getFieldDataByCondition("AccountRegister", "ar_id", "ar_sourcetype='出账申请' and ar_sourceid=" + aa_id);
		if (ar != null) {
			accountRegisterBankService.deleteAccountRegister(Integer.parseInt(ar.toString()), "AccountRegister!Bank");
		}
		baseDao.execute("delete from REIMBURSEMENTPLAN where RP_AAID=" + aa_id);
		// 删除利息单
		baseDao.execute("delete from FSINTEREST where in_aacode='" + status[1] + "'");
		baseDao.execute("update ACCOUNTAPPLY set aa_interest=0,aa_interestpay=0,aa_leftamount=aa_dueamount where aa_id=" + aa_id);
		handlerService.beforeResAudit(caller, new Object[] { aa_id });
		// 执行反审核操作
		baseDao.resAudit("AccountApply", "aa_id=" + aa_id, "aa_status", "aa_statuscode", "aa_auditman", "aa_auditdate");
		if ("保理额度申请出账".equals(status[6])) {
			baseDao.execute("update CUSTOMERQUOTA set CQ_YQUOTA=nvl(CQ_YQUOTA,0)-" + status[3] + " where cq_code='" + status[2] + "'");
		} else if ("核心企业额度申请出账".equals(status[6])) {
			baseDao.execute("update CUSTOMERQUOTAAPPLY set CA_YQUOTA=nvl(CA_YQUOTA,0)-" + status[3] + " where ca_code='" + status[2] + "'");
		}
		// 更新保理进度
		baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '出账审批',FS_STATUSDATE = null,FS_LOADDATE=null", "FS_CQCODE = '" + status[2]
				+ "'");
		Master master = SystemSession.getUser().getCurrentMaster();
		Master parentMaster = null;
		if (master != null && master.getMa_pid() != null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (null != parentMaster) {
			baseDao.execute("update " + parentMaster.getMa_user()
					+ ".FINBUSINAPPLY set FS_STATUS = '出账审批',FS_STATUSDATE=null,FS_LOADDATE=null where FS_CQCODE='" + status[2] + "'");
		}

		// 更新合同已使用合同金额
		baseDao.execute(
				"update FSSale set SA_USEDAMOUNT = NVL(SA_USEDAMOUNT,0) - (select nvl(aa_dueamount,0)/nvl(aa_wantamount,0)*nvl(aas_amount,0) from AccountApply,ACCOUNTAPPLYSA where aas_aaid = aa_id and nvl(aa_wantamount,0)<>0 and aa_id = ? and aas_sacode=code) where code in (select aas_sacode from ACCOUNTAPPLYSA where aas_aaid = ?)",
				aa_id, aa_id);

		// 记录操作
		baseDao.logger.resAudit(caller, "aa_id", aa_id);
		handlerService.afterResAudit(caller, new Object[] { aa_id });
	}

	/**
	 * 转银行登记
	 */
	@Override
	public JSONObject turnBankRegister(String caller, int aa_id) {
		JSONObject j = null;
		SqlRowList rs = baseDao.queryForRowSet("select * from AccountApply where aa_id=? ", aa_id);
		if (rs.next()) {
			if (StringUtil.hasText(rs.getObject("aa_catecode"))) {
				String aa_catecode = rs.getGeneralString("aa_catecode");
				String error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(ca_statuscode,' ')<>'已审核' and ca_isleaf=0",
						String.class, aa_catecode);
				if (error != null) {
					BaseUtil.showError("填写科目编号不存在，或者状态不等于已审核，或者不是末级科目，不允许转银行登记！");
				}
				error = baseDao.getJdbcTemplate().queryForObject(
						"select wmsys.wm_concat(ca_code) from Category where ca_code=? and nvl(CA_ISCASHBANK,0)=0", String.class,
						aa_catecode);
				if (error != null) {
					BaseUtil.showError("付款科目有误，请填写银行现金科目！");
				}
			} else {
				BaseUtil.showError("请填写银行科目!");
			}
			int ar_id = baseDao.getSeqId("ACCOUNTREGISTER_SEQ");
			String code = baseDao.sGetMaxNumber("AccountRegister", 2);
			baseDao.execute("insert into AccountRegister (ar_id,ar_code,ar_date,ar_recorddate,"
					+ "ar_payment,ar_type,ar_sourceid,ar_source,ar_sourcetype,ar_statuscode,ar_status,ar_recordman,"
					+ "ar_accountcode,ar_accountname,ar_accountcurrency,ar_cateid,ar_memo,ar_fscucode,ar_fscuname,ar_truster,ar_aacode) select "
					+ ar_id
					+ ", '"
					+ code
					+ "',aa_loandate,sysdate,aa_dueamount,'保理付款',aa_id,aa_code,'出账申请','ENTERING','"
					+ BaseUtil.getLocalMessage("ENTERING")
					+ "',aa_recorder,aa_catecode,aa_catedesc,ca_currency,ca_id,aa_remark,aa_custcode,aa_custname,aa_truster,aa_code from AccountApply,category where aa_catecode=ca_code and aa_id="
					+ aa_id);
			baseDao.execute("update accountregister set ar_accountrate=nvl((select cm_crrate from currencysmonth where cm_crname=ar_accountcurrency and cm_yearmonth=to_char(ar_date,'yyyymm')),1) where ar_id="
					+ ar_id);
			j = new JSONObject();
			j.put("ar_id", ar_id);
			j.put("ar_code", code);
			baseDao.execute("update AccountApply set aa_arid=" + ar_id + ",aa_arcode='" + code + "' where aa_id=" + aa_id);
			accountRegisterBankService.updateErrorString(ar_id);
			baseDao.logger.turn("转银行登记" + code, caller, "aa_id", aa_id);
		}
		return j;
	}

	@Override
	public void sendReimbursePlan() throws Exception {
		try {
			String sob = SpObserver.getSp();// 获取当前账套R

			String defaultSob = BaseUtil.getXmlSetting("defaultSob");// 默认账套
			String sql = "select * from " + defaultSob + ".SYS_SCHEDULETASK where CODE_ = 'B2B-accountTask' and enable_ = -1";
			ScheduleTask scheduleTask = baseDao.getJdbcTemplate().queryForObject(sql,
					new BeanPropertyRowMapper<ScheduleTask>(ScheduleTask.class));
			if (scheduleTask != null) {
				String masterStr = scheduleTask.getMaster_();
				if (!StringUtils.isEmpty(masterStr)) {
					String[] masterArray = masterStr.toUpperCase().replace(" ", "").split(",");
					for (String masterName : masterArray) {
						Master master = enterpriseService.getMasterByName(masterName);
						if (StringUtil.hasText(master.getMa_finwebsite())) {
							SpObserver.putSp(masterName);
							baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values" + "(sysdate,'开始发送还款计划到平台',"
									+ scheduleTask.getId_() + ")");
							List<Object[]> aacodes = baseDao
									.getFieldsDatasByCondition(
											"AccountApply",
											new String[] { "aa_code", "aa_aaid" },
											"nvl(aa_aaid,0)>0 and "
													+ "not EXISTS (select 1 from ReimbursementPlan where rp_aacode = aa_code and nvl(REIMBURSEMENTPLAN.ID,0)>0)");
							for (Object[] aacode : aacodes) {
								try {
									Map<String, String> params = new HashMap<String, String>();
									List<Map<String, Object>> plans = new ArrayList<Map<String, Object>>();
									SqlRowList rs = baseDao.queryForRowSet("select rp_id,rp_backdate,rp_currency,rp_principal,rp_interest,"
											+ "rp_iscloseoff,rp_truster from ReimbursementPlan where rp_aacode = ?", aacode[0]);
									while (rs.next()) {
										Map<String, Object> plan = new HashMap<String, Object>();
										plan.put("rp_id", rs.getGeneralInt("rp_id"));
										plan.put("rp_backdate", rs.getDate("rp_backdate"));
										plan.put("rp_currency", rs.getGeneralString("rp_currency"));
										plan.put("rp_principal", rs.getGeneralDouble("rp_principal"));
										plan.put("rp_interest", rs.getGeneralDouble("rp_interest"));
										plan.put("rp_iscloseoff", rs.getGeneralString("rp_iscloseoff"));
										plan.put("rp_truster", rs.getGeneralString("rp_truster"));
										plan.put("aaid", aacode[1]);
										plans.add(plan);
									}
									if (CollectionUtils.isEmpty(plans)) {
										continue;
									}

									params.put("list", FlexJsonUtil.toJsonArray(plans));
									Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite()
											+ "/repayment/erp/getfromuas?access_id=" + master.getMa_uu(), params, true,
											master.getMa_accesssecret());
									if (response.getStatusCode() != HttpStatus.OK.value()) {
										throw new Exception("连接平台失败," + response.getStatusCode());
									} else {
										String data = response.getResponseText();
										if (StringUtil.hasText(data)) {
											baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(data, "REIMBURSEMENTPLAN", "rp_id"));
										}
										String msg = "出账单：" + aacode[0] + ",还款计划" + plans.size() + "条";
										baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values" + "(sysdate,'"
												+ msg + "'," + scheduleTask.getId_() + ")");
									}
								} catch (Exception e) {
									e.printStackTrace();
									BaseUtil.showError("错误：" + e.getMessage());
								}
							}
							baseDao.execute("insert into SYS_SCHEDULETASKLOG(date_,remark_,scheduleid_) values" + "(sysdate,'结束发送还款计划到平台',"
									+ scheduleTask.getId_() + ")");
						}
					}
				}
			}

			SpObserver.putSp(sob);// 切回原账套
		} catch (EmptyResultDataAccessException e) {

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	@Override
	public void deleteFsOverdue(int od_id, String caller) {
		// 删除之前更新出账单
		Object aa_code = baseDao.getFieldDataByCondition("FsOverdue", "od_aacode", "od_id=" + od_id);
		if (aa_code != null) {
			String dets = baseDao.getJdbcTemplate().queryForObject(
					"select wm_concat(re_code) from FsRepayment where re_aacode=? and nvl(re_odamount,0)>0", String.class, aa_code);
			if (dets != null) {
				BaseUtil.showError("逾期单对应的出账申请已录入还款申请" + dets + "，请先删除还款申请，再删除逾期单！");
			}
			baseDao.execute("update accountapply set aa_isoverdue='否' where aa_code='" + aa_code + "'");
		}
		handlerService.handler(caller, "delete", "before", new Object[] { od_id });
		// 删除逾期单
		baseDao.deleteById("FsOverdue", "od_id", od_id);
		baseDao.logger.delete(caller, "od_id", od_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { od_id });
	}

}

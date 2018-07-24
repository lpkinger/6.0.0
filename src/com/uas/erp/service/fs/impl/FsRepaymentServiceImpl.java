package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.fs.FsRepaymentService;

@Service
public class FsRepaymentServiceImpl implements FsRepaymentService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Override
	public void saveFsRepayment(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 当前编号的记录已经存在,不能新增!
		handlerService.handler(caller, "save", "before", new Object[] { store });
		int count = baseDao.getCount("select count(1) from FsRepayment where re_aacode='" + store.get("re_aacode") + "' and re_code<>'"
				+ store.get("re_code") + "' and re_statuscode<>'AUDITED'");
		if (count > 0) {
			BaseUtil.showError("出账申请" + store.get("re_aacode") + "存在其他未审核的还款申请单，不允许保存！");
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "FsRepayment"));
		baseDao.logger.save(caller, "re_id", store.get("re_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void updateFsRepayment(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "FsRepayment", "re_id"));
		// 记录操作
		baseDao.logger.update(caller, "re_id", store.get("re_id"));
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteFsRepayment(int re_id, String caller) {
		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { re_id });
		// 删除主表内容
		baseDao.deleteById("FsRepayment", "re_id", re_id);
		baseDao.logger.delete(caller, "re_id", re_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { re_id });
	}

	@Override
	public void submitFsRepayment(int re_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		Object[] status = baseDao.getFieldsDataByCondition("FsRepayment left join accountapply on re_aacode=aa_code", new String[] {
				"re_statuscode", "nvl(re_thisamount,0)", "nvl(re_odinterest,0)", "aa_class", "nvl(re_total,0)" }, "re_id=" + re_id);
		StateAssert.submitOnlyEntering(status[0]);
		double thispayamount = NumberUtil.formatDouble(Double.parseDouble(status[1].toString()), 2);
		double overlx = NumberUtil.formatDouble(Double.parseDouble(status[2].toString()), 2);
		double amount = NumberUtil.formatDouble(Double.parseDouble(status[4].toString()), 2);
		if ("保理额度申请出账".equals(status[3])) {
			// 逾期利息
			if (thispayamount > 0 && overlx > 0 && thispayamount < overlx) {
				BaseUtil.showError("不允许部分归还逾期利息！");
			}
		} else if ("核心企业额度申请出账".equals(status[3])) {
			if (thispayamount > 0 && thispayamount != amount) {
				BaseUtil.showError("票据类出账本次还款金额必须等于应还金额合计" + amount + "！");
			}
		}

		// 执行提交前的其它逻辑
		handlerService.handler(caller, "commit", "before", new Object[] { re_id });
		// 执行提交操作
		baseDao.submit("FsRepayment", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.submit(caller, "re_id", re_id);
		// 执行提交后的其它逻辑
		handlerService.handler(caller, "commit", "after", new Object[] { re_id });
	}

	@Override
	public void resSubmitFsRepayment(int re_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("FsRepayment", "re_statuscode", "re_id=" + re_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { re_id });
		// 执行反提交操作
		baseDao.resOperate("FsRepayment", "re_id=" + re_id, "re_status", "re_statuscode");
		// 记录操作
		baseDao.logger.resSubmit(caller, "re_id", re_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { re_id });
	}

	@Override
	@Transactional
	public void auditFsRepayment(int re_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("FsRepayment",
				new String[] { "re_statuscode", "re_kind", "re_code", "re_aacode" }, "re_id=" + re_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { re_id });
		// 存储过程
		String res = baseDao.callProcedure("FS_REPAYMENT", new Object[] { re_id });
		if (res != null && !res.trim().equals("") && !"OK".equals(res.toUpperCase())) {
			BaseUtil.showError(res);
		}
		Master master = SystemSession.getUser().getCurrentMaster();
		Object[] apply = null;
		if ("出账单".equals(status[1].toString())) {
			apply = baseDao.getFieldsDataByCondition("ReimbursementApply left join AccountApply on ra_aacode = aa_code", new String[] {
					"aa_cacode", "aa_custcode", "aa_mfcustcode", "ra_backprincipal", "aa_aaid", "aa_iscloseoff" }, "ra_recode='"
					+ status[2] + "' and ra_kind = '出账单'");
		} else if ("逾期单".equals(status[1].toString())) {
			apply = baseDao.getFieldsDataByCondition(
					"ReimbursementApply left join FsOverdue on ra_odcode = od_code left join AccountApply on od_aacode = aa_code",
					new String[] { "aa_cacode", "aa_custcode", "aa_mfcustcode", "ra_backprincipal", "aa_aaid", "od_iscloseoff" },
					"ra_recode='" + status[2] + "' and ra_kind = '逾期单'");
		}
		if (apply != null) {
			Object[] cust = baseDao.getFieldsDataByCondition("CustomerQuota inner join FSMFCUSTINFO on cq_id = MF_CQID", new String[] {
					"mf_sourcecode", "mf_custname", "cq_finid" }, "cq_code = '" + apply[0] + "' and mf_custcode = '" + apply[2] + "'");
			if (null != cust) {
				try {
					if (null != cust[2] && null != apply[4]) {
						Object uu = baseDao.getFieldDataByCondition("CustomerInfor", "cu_enuu", "cu_code = '" + apply[1]
								+ "' and nvl(cu_b2benable,0)<>0");
						if (null != uu) {
							Map<String, String> params = new HashMap<String, String>();
							Map<String, Object> sellerQuota = new HashMap<String, Object>();
							sellerQuota.put("faid", cust[2]);
							sellerQuota.put("cq_custname", cust[1]);
							sellerQuota.put("cq_dueamount", Double.parseDouble(apply[3].toString()) * (-1));
							params.put("sellerQuota", FlexJsonUtil.toJson(sellerQuota));
							params.put("aaid", String.valueOf(apply[4]));
							String iscloseoff = null;
							if (apply[5] != null && "是".equals(apply[5].toString())) {
								iscloseoff = "已结清";
							} else {
								iscloseoff = "未结清";
							}
							params.put("iscloseoff", iscloseoff);
							Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite()
									+ "/sellerquota/erp/updateQuota?access_id=" + master.getMa_uu(), params, true,
									master.getMa_accesssecret());
							if (response.getStatusCode() != HttpStatus.OK.value()) {
								throw new Exception("连接平台失败," + response.getStatusCode());
							}
						}
					} else {
						SqlRowList rs = baseDao.queryForRowSet(
								"select cu_webserver,cu_whichsystem,cu_secret FROM CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0",
								apply[1]);
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
							params.put("cqcode", String.valueOf(apply[0]));
							params.put("custcode", String.valueOf(cust[0]));
							params.put("custname", String.valueOf(cust[1]));
							params.put("amount", String.valueOf(Double.parseDouble(apply[3].toString()) * (-1)));
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
		}

		try {
			Map<String, String> params = new HashMap<String, String>();
			List<Map<String, Object>> plans = new ArrayList<Map<String, Object>>();
			Object aaid = baseDao.getFieldDataByCondition("AccountApply", "aa_aaid", "aa_code ='" + status[3] + "'");
			if (aaid != null) {
				SqlRowList rs = baseDao.queryForRowSet("select rp_id,rp_backdate,rp_currency,rp_principal,rp_interest,"
						+ "rp_iscloseoff,rp_truster,id from ReimbursementPlan where rp_aacode = ?", status[3]);
				while (rs.next()) {
					Map<String, Object> plan = new HashMap<String, Object>();
					plan.put("id", rs.getGeneralInt("id"));
					plan.put("rp_id", rs.getGeneralInt("rp_id"));
					plan.put("rp_backdate", rs.getDate("rp_backdate"));
					plan.put("rp_currency", rs.getGeneralString("rp_currency"));
					plan.put("rp_principal", rs.getGeneralDouble("rp_principal"));
					plan.put("rp_interest", rs.getGeneralDouble("rp_interest"));
					plan.put("rp_iscloseoff", rs.getGeneralString("rp_iscloseoff"));
					plan.put("rp_truster", rs.getGeneralString("rp_truster"));
					plan.put("aaid", aaid);
					plans.add(plan);
				}
				if (!CollectionUtils.isEmpty(plans)) {
					params.put("list", FlexJsonUtil.toJsonArray(plans));
					Response response = HttpUtil.sendPostRequest(master.getMa_finwebsite() + "/repayment/erp/getfromuas?access_id="
							+ master.getMa_uu(), params, true, master.getMa_accesssecret());
					if (response.getStatusCode() != HttpStatus.OK.value()) {
						throw new Exception("连接平台失败," + response.getStatusCode());
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误：" + e.getMessage());
		}

		baseDao.audit("FsRepayment", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditdate", "re_auditman");
		// 记录操作
		baseDao.logger.audit(caller, "re_id", re_id);
		// 执行审核后的其它逻辑
		handlerService.handler(caller, "audit", "after", new Object[] { re_id });
	}

	@Override
	public void resAuditFsRepayment(int re_id, String caller) {
		// 只能对状态为[已审核]的单据进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("FsRepayment", new String[] { "re_statuscode", "re_code" }, "re_id=" + re_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("FsRepayment", re_id);
		handlerService.beforeResAudit(caller, new Object[] { re_id });
		// 执行反审核操作
		baseDao.resAudit("FsRepayment", "re_id=" + re_id, "re_status", "re_statuscode", "re_auditman", "re_auditdate");
		// 记录操作
		baseDao.logger.resAudit(caller, "re_id", re_id);
		handlerService.afterResAudit(caller, new Object[] { re_id });
	}
}

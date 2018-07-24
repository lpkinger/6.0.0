package com.uas.erp.service.fs.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

import com.uas.b2b.model.CustomerInfo.AccountInfo;
import com.uas.b2b.model.CustomerInfo.BusinessConditionInfo;
import com.uas.b2b.model.CustomerInfo.FinanceConditionInfo;
import com.uas.b2b.model.CustomerInfo.ProductMixInfo;
import com.uas.b2b.model.CustomerInfo.PurcCustInfo;
import com.uas.b2b.model.CustomerInfo.PurcCustInfo.MFCustInfoDetail;
import com.uas.b2b.model.CustomerInfo.UpdowncastInfo;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HandlerService;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.core.support.StateAssert;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fs.CustomerQuotaService;

@Service
public class CustomerQuotaServiceImpl implements CustomerQuotaService {
	@Autowired
	private BaseDao baseDao;
	@Autowired
	private HandlerService handlerService;

	@Autowired
	private EnterpriseService enterpriseService;

	@Override
	public void saveCustomerQuota(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		store.put("cq_isvalid", "否");
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		baseDao.execute(SqlUtil.getInsertSqlByMap(store, "CustomerQuota"));
		baseDao.logger.save(caller, "cq_id", store.get("cq_id"));
		baseDao.saveClob("CustomerQuota", clobFields, clobStrs, "cq_id=" + store.get("cq_id"));
		// 执行保存后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
		baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { store.get("cq_id"), "保理额度申请" });
	}

	private Master getFsMaster() {
		Master master = SystemSession.getUser().getCurrentMaster();
		Master FSMaster = null;
		if (master != null && !StringUtils.isEmpty(master.getMa_soncode())) {// 父级账套
			String fsmaster = baseDao.getDBSetting("FS_master");
			if (null != fsmaster) {
				String sob = SpObserver.getSp();
				FSMaster = enterpriseService.getMasterByName(fsmaster);
				SpObserver.putSp(sob);
			}
		} else {
			FSMaster = master;
		}
		return FSMaster;
	}

	@Override
	public void updateCustomerQuota(String formStore, String caller) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		handlerService.handler(caller, "save", "before", new Object[] { store });
		store.put("cq_isvalid", "否");
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		baseDao.execute(SqlUtil.getUpdateSqlByFormStore(store, "CustomerQuota", "cq_id"));
		if (clobFields.size() > 0) {
			baseDao.saveClob("CustomerQuota", clobFields, clobStrs, "cq_id=" + store.get("cq_id"));
		}
		baseDao.execute("update CustomerQuota set cq_recorder='" + SystemSession.getUser().getEm_name() + "' where cq_id="
				+ store.get("cq_id") + " and nvl(cq_recorder,' ')=' '");
		// 记录操作
		baseDao.logger.update(caller, "cq_id", store.get("cq_id"));
		baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { store.get("cq_id"), "保理额度申请" });
		// 执行修改后的其它逻辑
		handlerService.handler(caller, "save", "after", new Object[] { store });
	}

	@Override
	public void deleteCustomerQuota(int cq_id, String caller) {

		// 执行删除前的其它逻辑
		handlerService.handler(caller, "delete", "before", new Object[] { cq_id });
		Object code = baseDao.getFieldDataByCondition("CustomerQuota", "cq_code", "cq_id = " + cq_id);
		// 删除主表内容
		baseDao.deleteById("CustomerQuota", "cq_id", cq_id);
		// 基本情况
		baseDao.deleteById("BorrowerSurveyBase ", "bs_caid", cq_id);
		baseDao.deleteByCondition("BORROWERSHAREHOLDER", "bs_caid=" + cq_id);
		baseDao.deleteByCondition("BORROWERINVERSTMENT", "bi_caid=" + cq_id);
		baseDao.deleteByCondition("BORROWERELATION", "br_caid=" + cq_id);
		baseDao.deleteByCondition("FSMFCUSTINFO", "mf_cqid=" + cq_id);
		baseDao.deleteByCondition("FSMFCUSTINFODET", "mfd_cqid=" + cq_id);
		// 资信情况分析
		baseDao.deleteById("CustCreditStatus ", "cc_caid", cq_id);
		baseDao.deleteByCondition("CUSTOMERBORROWING", "cb_caid=" + cq_id);
		baseDao.deleteByCondition("CUSTOMERGUARANTY", "cg_caid=" + cq_id);
		// 收入及盈利情况核实
		// baseDao.deleteById("COSTFEEAUDIT ", "cf_caid", cq_id);
		baseDao.deleteByCondition("BANKFLOWAUDIT", "bf_caid=" + cq_id);
		baseDao.deleteByCondition("PAYTAXESAUDIT", "pt_caid=" + cq_id);
		// 调查结论
		// baseDao.deleteById("SURVEYCONCLUSION ", "sc_caid", cq_id);
		// 经营情况
		baseDao.deleteById("BUSINESSCONDITION ", "bc_id", cq_id);
		baseDao.deleteById("BC_ProductMix ", "pm_bcid", cq_id);
		baseDao.deleteById("bc_updowncust ", "udc_bcid", cq_id);
		baseDao.deleteById("BC_ProposedFinance ", "pf_bcid", cq_id);
		baseDao.deleteById("BC_ProjectPlan ", "pp_bcid", cq_id);
		baseDao.deleteById("BC_ImportExport ", "ie_bcid", cq_id);
		baseDao.deleteById("bc_yeardeal ", "yd_bcid", cq_id);
		// 资产负责情况
		baseDao.deleteById("ASSETSLIABILITIES ", "al_id", cq_id);
		baseDao.deleteById("FINANCCONDITION ", "fc_caid", cq_id);
		baseDao.deleteById("AL_ACCOUNTINFOR ", "ai_alid", cq_id);
		baseDao.deleteById("AL_ACCOUNTINFORDETAIL ", "aid_alid", cq_id);
		baseDao.deleteById("Guarantee ", "gu_caid", cq_id);
		// 买方客户信息
		baseDao.deleteById("FSMFCUSTINFO ", "mf_cqid", cq_id);
		baseDao.deleteById("FSMFCustInfoDet ", "mfd_cqid", cq_id);
		// 审批结果报告
		baseDao.deleteById("fs_report ", "re_cqid", cq_id);
		baseDao.deleteById("fs_resultsreport ", "rr_caid", cq_id);
		// 申请进度
		List<String> sql = new ArrayList<String>();
		sql.add("delete from FINBUSINAPPLY where fs_cqcode = '" + code + "'");
		Master fSMaster = getFsMaster();
		if (fSMaster != null) {
			sql.add("delete from " + fSMaster.getMa_name() + ".FINBUSINAPPLY where fs_cqcode = '" + code + "'");
		}
		baseDao.execute(sql);

		baseDao.logger.delete(caller, "cq_id", cq_id);
		// 执行删除后的其它逻辑
		handlerService.handler(caller, "delete", "after", new Object[] { cq_id });
	}

	@Override
	public void submitCustomerQuota(int cq_id, String caller) {
		// 只能对状态为[在录入]的表单进行提交操作!
		SqlRowList rs = baseDao.queryForRowSet(
				"select cq_statuscode,cq_custname,cq_quotatype,cq_quota,cq_nomfcust FROM CustomerQuota where cq_id = ? ", cq_id);
		if (rs.next()) {
			if (rs.getGeneralInt("cq_nomfcust") == 0) {
				baseDao.execute("update CustomerQuota set cq_quota=nvl((select sum(mf_credit) from FSMFCUSTINFO where mf_cqid=cq_id),0) where cq_id="
						+ cq_id);
			}
			if (rs.getGeneralDouble("cq_quota") == 0) {
				BaseUtil.showError("审批额度不能为0！");
			}
			if (rs.getGeneralInt("cq_nomfcust") == 0) {
				int count = baseDao.getCount("select count(1) from FSMFCUSTINFO where mf_cqid=" + cq_id);
				if (count == 0) {
					BaseUtil.showError("该客户没有填写买方客户资料！");
				}
			}
			StateAssert.submitOnlyEntering(rs.getObject("cq_statuscode"));
			// 检测企业信用风险评估
			boolean bool = baseDao.checkByCondition("CustCreditRatingApply", "cra_cuvename='" + rs.getObject("cq_custname")
					+ "' and cra_statuscode='AUDITED' and cra_valid = 'VALID' and cra_type ='企业信用风险'");
			if (bool) {
				BaseUtil.showError("该客户没有有效的企业信用风险评级报告，请先进行企业信用风险评级！");
			}
			// 检测企业经营风险评估
			bool = baseDao.checkByCondition("CustCreditRatingApply", "cra_cuvename='" + rs.getObject("cq_custname")
					+ "' and cra_statuscode='AUDITED' and cra_valid = 'VALID' and cra_type ='信用评级申请'");
			if (bool) {
				BaseUtil.showError("该客户没有有效的信用评级报告，请先进行信用评级！");
			} else {
				baseDao.execute("update CustomerQuota set (cq_creditlevel,cq_creditscore)=(select cra_creditrating,cra_score from "
						+ "CUSTOMERINFOR_VIEW where nvl(cra_creditrating,' ')<>' ' and cu_code=cq_custcode) where cq_id=" + cq_id);
			}
			// 执行提交前的其它逻辑
			handlerService.handler(caller, "commit", "before", new Object[] { cq_id });
			// 执行提交操作
			baseDao.submit("CustomerQuota", "cq_id=" + cq_id, "cq_status", "cq_statuscode");
			baseDao.execute("update FSMFCUSTINFO set MF_STATUS='" + BaseUtil.getLocalMessage("COMMITED")
					+ "',MF_STATUSCODE='COMMITED' WHERE mf_cqid=" + cq_id);
			// 记录操作
			baseDao.logger.submit(caller, "cq_id", cq_id);
			// 执行提交后的其它逻辑
			handlerService.handler(caller, "commit", "after", new Object[] { cq_id });
		}
	}

	@Override
	public void resSubmitCustomerQuota(int cq_id, String caller) {
		// 只能对状态为[已提交]的表单进行反提交操作!
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		StateAssert.resSubmitOnlyCommited(status);
		handlerService.handler(caller, "resCommit", "before", new Object[] { cq_id });
		// 执行反提交操作
		baseDao.resOperate("CustomerQuota", "cq_id=" + cq_id, "cq_status", "cq_statuscode");
		baseDao.execute("update FSMFCUSTINFO set MF_STATUS='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',MF_STATUSCODE='ENTERING' WHERE mf_cqid=" + cq_id);
		// 记录操作
		baseDao.logger.resSubmit(caller, "cq_id", cq_id);
		handlerService.handler(caller, "resCommit", "after", new Object[] { cq_id });
	}

	@Override
	public void auditCustomerQuota(int cq_id, String caller) {
		// 只能对已提交进行审核操作
		Object[] status = baseDao.getFieldsDataByCondition("CustomerQuota",
				"cq_statuscode,cq_approval,cq_code,cq_custcode,cq_finid,cq_approval,NVL(cq_nomfcust,0)", "cq_id=" + cq_id);
		StateAssert.auditOnlyCommited(status[0]);
		// 执行审核前的其它逻辑
		handlerService.handler(caller, "audit", "before", new Object[] { cq_id });

		backInfoToB2B(cq_id);

		backQuota(cq_id);

		audit(cq_id, caller);
	}

	private void backQuota(int id) {
		try {
			SqlRowList rs = baseDao.queryForRowSet("select * from CustomerQuota where cq_id=?", id);
			if (rs.next()) {
				if (rs.getGeneralInt("cq_finid") != 0) {
					Master FSMaster = getFsMaster();
					Object uu = baseDao.getFieldDataByCondition("CustomerInfor", "cu_enuu", "cu_code = '" + rs.getObject("cq_custcode")
							+ "' and nvl(cu_b2benable,0)<>0");
					if (null != FSMaster && null != uu) {
						Map<String, String> params = new HashMap<String, String>();
						params.put("faid", rs.getObject("cq_finid").toString());
						if (rs.getGeneralInt("cq_approval") == 1 && rs.getGeneralInt("cq_nomfcust") == 0) {
							List<Object[]> custs = baseDao.getFieldsDatasByCondition("FSMFCUSTINFO", new String[] { "MF_CUSTNAME",
									"MF_CREDIT" }, "MF_CQID = " + id);
							if (custs.size() < 1) {
								BaseUtil.showError("买方客户为空，不能进行应收账款转让！");
							}
							List<Map<String, Object>> mfcusts = new ArrayList<Map<String, Object>>();

							for (Object[] cust : custs) {
								Map<String, Object> mfcust = new HashMap<String, Object>();
								mfcust.put("cq_custname", cust[0]);
								mfcust.put("cq_quota", cust[1]);
								mfcust.put("cq_dueamount", 0);
								mfcust.put("cq_spamount", cust[1]);
								mfcust.put("cq_uu", uu);
								mfcust.put("cq_startdate", new Date());
								Calendar calendar = new GregorianCalendar();
								calendar.setTime(new Date());
								calendar.add(Calendar.MONTH, 12);
								mfcust.put("cq_enddate", calendar.getTime());
								mfcusts.add(mfcust);
							}
							params.put("list", FlexJsonUtil.toJsonArray(mfcusts));
							params.put("status", "204");
						} else {
							params.put("status", "205");
						}
						Response response = HttpUtil.sendPostRequest(FSMaster.getMa_finwebsite() + "/sellerquota/erp/getfromuas?access_id="
								+ FSMaster.getMa_uu(), params, true, FSMaster.getMa_accesssecret());
						if (response.getStatusCode() != HttpStatus.OK.value()) {
							throw new Exception("连接平台失败," + response.getStatusCode());
						}
					} else if (rs.getGeneralInt("cq_approval") == 1) {
						SqlRowList rs1 = baseDao.queryForRowSet(
								"select cu_webserver,cu_whichsystem,cu_secret FROM CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0",
								rs.getObject("cq_custcode"));
						if (rs.next()) {
							String web = rs1.getGeneralString("cu_webserver");
							String whichsys = rs1.getGeneralString("cu_whichsystem");
							String secret = rs1.getGeneralString("cu_secret");
							if (!StringUtil.hasText(web) || !StringUtil.hasText(whichsys)) {
								BaseUtil.showError("客户资料的网址或账套不明，无法自动进行应收账款转让！");
							}
							if (!StringUtil.hasText(secret)) {
								BaseUtil.showError("密钥为空，不能自动进行应收账款转让！");
							}
							Map<String, String> params = new HashMap<String, String>();
							List<Object[]> custs = baseDao.getFieldsDatasByCondition("FSMFCUSTINFO", new String[] { "MF_SOURCECODE",
									"MF_CUSTNAME", "MF_CREDIT" }, "MF_CQID = " + id);
							if (rs.getGeneralInt("cq_nomfcust") == 0 && custs.size() < 1) {
								BaseUtil.showError("买方客户为空，不能进行应收账款转让！");
							}
							if (custs.size() < 1) {
								return;
							}
							List<Map<String, Object>> mfcusts = new ArrayList<Map<String, Object>>();

							Master master = SystemSession.getUser().getCurrentMaster();
							for (Object[] cust : custs) {
								Map<String, Object> mfcust = new HashMap<String, Object>();
								mfcust.put("cuc_custcode", cust[0]);
								mfcust.put("cuc_custname", cust[1]);
								mfcust.put("cuc_credit", cust[2]);
								mfcust.put("cuc_cqcode", rs.getObject("cq_code"));
								mfcust.put("cuc_fccode", master.getMa_name());
								mfcusts.add(mfcust);
							}
							params.put("mfcusts", FlexJsonUtil.toJsonArray(mfcusts));

							Response response = HttpUtil.sendPostRequest(web + "/openapi/factoring/recBalanceAssign.action?master="
									+ whichsys, params, true, secret);
							if (response.getStatusCode() != HttpStatus.OK.value()) {
								throw new Exception("连接客户账套失败," + response.getStatusCode());
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			BaseUtil.showError("错误：" + e.getMessage());
		}
	}

	private void backInfoToB2B(int id) {
		Long cuid = baseDao.getFieldValue("CustomerQuota left join CustomerInfor on cq_custcode = cu_code", "id", "cq_id = " + id
				+ " and nvl(cu_b2benable,0)<>0 and cu_enuu is not null and cq_finid is not null", Long.class);
		if (cuid != null) {
			try {
				Master FSMaster = getFsMaster();
				;
				if (null != FSMaster) {
					Map<String, String> params = new HashMap<String, String>();

					List<PurcCustInfo> mfCusts = baseDao.getJdbcTemplate().query(
							"select a.*,mf_id erpId from FSMFCUSTINFO a where mf_cqid = ? order by mf_detno",
							new BeanPropertyRowMapper<PurcCustInfo>(PurcCustInfo.class), id);
					List<MFCustInfoDetail> mfCustInfoDetailList = new ArrayList<MFCustInfoDetail>();
					for (PurcCustInfo purcCustInfo : mfCusts) {
						purcCustInfo.setCusId(cuid);
						List<MFCustInfoDetail> pDetails = baseDao
								.getJdbcTemplate()
								.query("select id,"
										+ purcCustInfo.getId()
										+ " mfId,mfd_year,mfd_amount,mfd_chargeamount,mfd_discountamount,mfd_overdue,mfd_id erpId from FSMFCUSTINFODET a where mfd_mfid = ?",
										new BeanPropertyRowMapper<MFCustInfoDetail>(MFCustInfoDetail.class), purcCustInfo.getErpId());
						mfCustInfoDetailList.addAll(pDetails);
					}
					params.put("mfCust", FlexJsonUtil.toJsonArray(mfCusts));
					params.put("mfCustInfoDetailList", FlexJsonUtil.toJsonArray(mfCustInfoDetailList));

					try {
						BusinessConditionInfo businessConditionInfo = baseDao.getJdbcTemplate().queryForObject(
								"select a.*,bc_id erpId from BUSINESSCONDITION a where bc_id = ?",
								new BeanPropertyRowMapper<BusinessConditionInfo>(BusinessConditionInfo.class), id);
						businessConditionInfo.setCusId(cuid);
						params.put("businessCondition", FlexJsonUtil.toJson(businessConditionInfo));
					} catch (EmptyResultDataAccessException e) {
					}

					List<ProductMixInfo> productMixInfos = baseDao.getJdbcTemplate().query(
							"select a.*,pm_id erpId from BC_ProductMix a where pm_bcid = ? order by pm_detno",
							new BeanPropertyRowMapper<ProductMixInfo>(ProductMixInfo.class), id);
					for (ProductMixInfo productMixInfo : productMixInfos) {
						productMixInfo.setCusId(cuid);
					}
					params.put("prouductMixe", FlexJsonUtil.toJsonArray(productMixInfos));

					List<UpdowncastInfo> updowncastInfos = baseDao.getJdbcTemplate().query(
							"select a.*,udc_id erpId from BC_UPDOWNCUST a where udc_bcid = ? order by udc_detno",
							new BeanPropertyRowMapper<UpdowncastInfo>(UpdowncastInfo.class), id);
					for (UpdowncastInfo updowncastInfo : updowncastInfos) {
						updowncastInfo.setCusId(cuid);
					}
					params.put("updowncast", FlexJsonUtil.toJsonArray(updowncastInfos));

					try {
						FinanceConditionInfo financeConditionInfo = baseDao.getJdbcTemplate().queryForObject(
								"select a.*,fc_caid erpId from FINANCCONDITION a where fc_caid = ?",
								new BeanPropertyRowMapper<FinanceConditionInfo>(FinanceConditionInfo.class), id);
						financeConditionInfo.setCusId(cuid);
						financeConditionInfo.setMonth(financeConditionInfo.getFc_yeatmonth() % 100);
						financeConditionInfo.setYear(financeConditionInfo.getFc_yeatmonth() / 100);
						params.put("financeCondition", FlexJsonUtil.toJson(financeConditionInfo));
					} catch (EmptyResultDataAccessException e) {
					}

					List<AccountInfo> accountInfos = baseDao.getJdbcTemplate().query(
							"select a.*,ai_id erpId from AL_ACCOUNTINFOR a where ai_alid = ? order by ai_detno asc",
							new BeanPropertyRowMapper<AccountInfo>(AccountInfo.class), id);
					for (AccountInfo accountInfo : accountInfos) {
						accountInfo.setCusId(cuid);
					}
					params.put("accountList", FlexJsonUtil.toJsonArray(accountInfos));
					Response response = HttpUtil.sendPostRequest(FSMaster.getMa_finwebsite() + "/customer/erp/updateinfo2?access_id="
							+ FSMaster.getMa_uu(), params, true, FSMaster.getMa_accesssecret());
					if (response.getStatusCode() != HttpStatus.OK.value()) {
						throw new Exception("连接平台失败," + response.getStatusCode());
					} else {
						String data = response.getResponseText();
						if (StringUtil.hasText(data)) {
							JSONObject result = JSONObject.fromObject(data);
							if (result.has("mfCust")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("mfCust"), "FSMFCUSTINFO", "mf_id"));
							}
							if (result.has("mfCustInfoDetailList")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("mfCustInfoDetailList"),
										"FSMFCUSTINFODET", "mfd_id"));
							}
							if (result.has("businessCondition")) {
								baseDao.execute(SqlUtil.getUpdateSqlByFormStore(
										BaseUtil.parseFormStoreToMap(result.getString("businessCondition")), "BUSINESSCONDITION", "bc_id"));
							}
							if (result.has("prouductMixe")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("prouductMixe"), "BC_ProductMix", "pm_id"));
							}
							if (result.has("updowncast")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("updowncast"), "BC_UPDOWNCUST", "udc_id"));
							}
							if (result.has("financeCondition")) {
								baseDao.execute(SqlUtil.getUpdateSqlByFormStore(
										BaseUtil.parseFormStoreToMap(result.getString("financeCondition")), "FINANCCONDITION", "fc_caid"));
							}
							if (result.has("accountList")) {
								baseDao.execute(SqlUtil.getUpdateSqlbyGridStore(result.getString("accountList"), "AL_ACCOUNTINFOR", "ai_id"));
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("错误：" + e.getMessage());
			}
		}
	}

	// 审核逻辑
	private void audit(int cq_id, String caller) {
		SqlRowList rs = baseDao.queryForRowSet("select * from CustomerQuota where cq_id=?", cq_id);
		if (rs.next()) {
			Object[] oth = baseDao.getFieldsDataByCondition("CustomerQuota", new String[] { "cq_id", "cq_code" },
					"cq_isvalid='是' and cq_id<>" + cq_id + " and cq_custcode='" + rs.getObject("cq_custcode") + "'");
			if (oth != null) {
				baseDao.execute("update CustomerQuota set cq_isvalid='否' where cq_id=" + oth[0]);
				baseDao.execute("update CustomerQuota set CQ_CREDITCOND='续作',CQ_OLDCODE='" + oth[1] + "' where cq_id=" + cq_id);
			} else {
				baseDao.execute("update CustomerQuota set CQ_CREDITCOND='新增' where cq_id=" + cq_id);
			}

			baseDao.audit("CustomerQuota", "cq_id=" + cq_id, "cq_status", "cq_statuscode", "cq_auditdate", "cq_auditman");
			baseDao.execute("update FSMFCUSTINFO set MF_STATUS='" + BaseUtil.getLocalMessage("AUDITED")
					+ "',MF_STATUSCODE='AUDITED' WHERE mf_cqid=" + cq_id);
			baseDao.execute("update CustomerQuota set cq_isvalid='是' where cq_id=" + cq_id);
			// 更新保理进度
			Object approval = null;//
			if (rs.getGeneralInt("cq_approval") != 0) {
				approval = "出账审批";
			} else {
				approval = "申请取消";
			}
			baseDao.updateByCondition("FINBUSINAPPLY",
					"FS_STATUS = '" + approval + "', FS_ACCEPTDATE = " + DateUtil.parseDateToOracleString(Constant.YMD_HMS, new Date()),
					"FS_CQCODE = '" + rs.getObject("cq_code") + "' and FS_STATUSDATE is null");
			baseDao.procedure("FS_CREATERESULTSREPORT", new Object[] { cq_id });
			// 记录操作
			baseDao.logger.audit(caller, "cq_id", cq_id);
			// 执行审核后的其它逻辑
			handlerService.handler(caller, "audit", "after", new Object[] { cq_id });
		}
	}

	@Override
	public void resAuditCustomerQuota(int cq_id, String caller) {
		// 只能对状态为[已审核]的采购单进行反审核操作!
		Object[] status = baseDao.getFieldsDataByCondition("CustomerQuota", "cq_statuscode,cq_code", "cq_id=" + cq_id);
		StateAssert.resAuditOnlyAudit(status[0]);
		baseDao.resAuditCheck("CustomerQuota", cq_id);
		String dets = baseDao.getJdbcTemplate().queryForObject(
				"select wm_concat(aa_code) from AccountApply where aa_cacode=? and aa_statuscode = 'AUDITED'", String.class, status[1]);
		if (dets != null) {
			BaseUtil.showError("已存在已审核的出账申请[" + dets + "]，不允许反审核！");
		}
		handlerService.beforeResAudit(caller, new Object[] { cq_id });
		// 执行反审核操作
		baseDao.resAudit("CustomerQuota", "cq_id=" + cq_id, "cq_status", "cq_statuscode", "cq_auditman", "cq_auditdate");
		baseDao.execute("update FSMFCUSTINFO set MF_STATUS='" + BaseUtil.getLocalMessage("ENTERING")
				+ "',MF_STATUSCODE='ENTERING' WHERE mf_cqid=" + cq_id);
		// 更新保理进度
		baseDao.updateByCondition("FINBUSINAPPLY", "FS_STATUS = '应收账款转让',FS_ACCEPTDATE = null,FS_STATUSDATE =null,FS_LOADDATE=null",
				"FS_CQCODE = '" + status[1] + "'");
		// 记录操作
		baseDao.logger.resAudit(caller, "cq_id", cq_id);
		handlerService.afterResAudit(caller, new Object[] { cq_id });
	}

	@Override
	public void getDefaultDatas(int cqid) {

		SqlRowList rs = baseDao
				.queryForRowSet(
						"select cu_id,cu_webserver,cu_whichsystem,to_char(cq_indate,'yyyy-MM-dd') applydate,cu_issys,cq_lastym,cu_secret FROM CustomerQuota left join CustomerInfor on cq_custcode = cu_code WHERE cq_id=?",
						cqid);
		if (rs.next()) {
			int cuid = rs.getInt("cu_id");
			String web = rs.getGeneralString("cu_webserver");
			String whichsys = rs.getGeneralString("cu_whichsystem");
			String secret = rs.getGeneralString("cu_secret");
			int issys = rs.getGeneralInt("cu_issys");
			try {
				boolean bool = baseDao.checkByCondition("BORROWERSHAREHOLDER", "bs_caid = " + cqid);
				if (bool) {
					baseDao.execute("INSERT INTO BORROWERSHAREHOLDER(bs_id,bs_caid,bs_detno,bs_name,bs_investratio,bs_mainbusiness,bs_paperstype,bs_paperscode) SELECT BORROWERSHAREHOLDER_SEQ.nextval,"
							+ cqid
							+ ",rownum,cs_name,cs_investratio,cs_mainbusiness,cs_paperstype,cs_paperscode FROM CUSTOMERSHAREHOLDER WHERE cs_cuid = "
							+ cuid);
				}
				bool = baseDao.checkByCondition("BORROWERINVERSTMENT", "bi_caid = " + cqid);
				if (bool) {
					baseDao.execute("INSERT INTO BORROWERINVERSTMENT(bi_id,bi_caid,bi_detno,bi_name,bi_investratio,bi_mainbusiness) SELECT BORROWERINVERSTMENT_SEQ.nextval,"
							+ cqid + ",rownum,ci_name,ci_investratio,ci_mainbusiness FROM CUSTOMERINVERSTMENT WHERE ci_cuid = " + cuid);
				}
				bool = baseDao.checkByCondition("BORROWERELATION", "br_caid = " + cqid);
				if (bool) {
					baseDao.execute("INSERT INTO BORROWERELATION(br_id,br_caid,br_detno,br_name,br_relation,br_mainbusiness) SELECT BORROWERELATION_SEQ.nextval,"
							+ cqid + ",rownum,cud_name,cud_relation,cud_product FROM CUSTOMERUDSTREAM WHERE cud_cuid = " + cuid);
				}
				if (issys == 0) {
					return;
				}
				if (!StringUtil.hasText(web) || !StringUtil.hasText(whichsys)) {
					BaseUtil.showError("客户资料的网址或账套不明，无法正常取数！");
				}
				if (!StringUtil.hasText(secret)) {
					BaseUtil.showError("密钥为空，不能自动取数！");
				}
				Map<String, String> params = new HashMap<String, String>();
				params.put("applydate", rs.getGeneralString("applydate"));
				params.put("lastym", rs.getGeneralString("cq_lastym"));
				bool = baseDao.checkByCondition("BANKFLOWAUDIT", "bf_caid = " + cqid);
				if (bool) {
					params.put("bankflow", "true");
				}
				bool = baseDao.checkByCondition("BC_ProductMix", "pm_bcid = " + cqid);
				if (bool) {
					params.put("productmix", "true");
				}
				bool = baseDao.checkByCondition("bc_updowncust", "udc_bcid = " + cqid);
				if (bool) {
					params.put("updowncust", "true");
				}
				// 货币资金
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='货币资金'");
				if (bool) {
					params.put("monetaryfund", "true");
				}
				// 应收账款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='应收账款'");
				if (bool) {
					params.put("accountinforar", "true");

				}
				bool = baseDao.checkByCondition("FINANCCONDITION", "fc_caid = " + cqid);
				if (bool) {
					params.put("financcondition", "true");
				}
				// 其他应收账款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='其他应收账款'");
				if (bool) {
					params.put("accountinforothar", "true");
				}
				// 预付账款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='预付账款'");
				if (bool) {
					params.put("accountinforpp", "true");
				}
				// 存货
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='存货'");
				if (bool) {
					params.put("accountinforinv", "true");
				}
				// 固定资产
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='固定资产'");
				if (bool) {
					params.put("accountinforfix", "true");
				}
				// 短期借款-贷款银行
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='短期借款-贷款银行'");
				if (bool) {
					params.put("accountinforlb", "true");
				}
				// 应付账款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='应付账款'");
				if (bool) {
					params.put("accountinforap", "true");
				}
				// 其他应付账款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='其他应付账款'");
				if (bool) {
					params.put("accountinforothap", "true");
				}
				// 长期借款
				bool = baseDao.checkByCondition("AL_ACCOUNTINFOR", "AI_ALID = " + cqid + " and ai_kind='长期借款'");
				if (bool) {
					params.put("accountinforlong", "true");
				}
				if (params.size() > 0) {
					Response response = HttpUtil.sendPostRequest(web + "/openapi/factoring/getDefaultDataS.action?master=" + whichsys,
							params, true, secret);
					if (response.getStatusCode() == HttpStatus.OK.value()) {
						String data = response.getResponseText();
						if (StringUtil.hasText(data)) {
							Map<Object, Object> result = BaseUtil.parseFormStoreToMap(data);
							// 获取收入及盈利情况-银行流水情况
							if (StringUtil.hasText(result.get("bankflow"))) {
								List<Map<Object, Object>> bankflow = BaseUtil.parseGridStoreToMaps(result.get("bankflow").toString());
								for (Map<Object, Object> map : bankflow) {
									map.put("bf_id", baseDao.getSeqId("BANKFLOWAUDIT_SEQ"));
									map.put("bf_caid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(bankflow, "BANKFLOWAUDIT"));
							}
							// 经营情况-主营产品/服务结构
							if (StringUtil.hasText(result.get("productmix"))) {
								List<Map<Object, Object>> productmix = BaseUtil.parseGridStoreToMaps(result.get("productmix").toString());
								for (Map<Object, Object> map : productmix) {
									map.put("pm_id", baseDao.getSeqId("BC_PRODUCTMIX_SEQ"));
									map.put("pm_bcid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(productmix, "BC_PRODUCTMIX"));
							}
							// 资产情况-货币资金
							if (StringUtil.hasText(result.get("monetaryfund"))) {
								List<Map<Object, Object>> monetaryfund = BaseUtil.parseGridStoreToMaps(result.get("monetaryfund")
										.toString());
								for (Map<Object, Object> map : monetaryfund) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(monetaryfund, "AL_ACCOUNTINFOR"));
							}
							// 资产情况-应收账款
							if (StringUtil.hasText(result.get("accountinforar"))) {
								List<Map<Object, Object>> accountinforar = BaseUtil.parseGridStoreToMaps(result.get("accountinforar")
										.toString());
								for (Map<Object, Object> map : accountinforar) {
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforar, "AL_ACCOUNTINFOR"));
							}
							// 账龄
							if (StringUtil.hasText(result.get("financcondition"))) {
								Map<Object, Object> financcondition = BaseUtil
										.parseFormStoreToMap(result.get("financcondition").toString());
								financcondition.put("fc_caid", cqid);
								String sql = SqlUtil.getInsertSqlByFormStore(financcondition, "FINANCCONDITION", new String[] {},
										new Object[] {});
								baseDao.execute(sql);
							}
							// 资产情况-应收账款-欠款明细
							if (StringUtil.hasText(result.get("accountinforardet"))) {
								List<Map<Object, Object>> accountinforardet = BaseUtil.parseGridStoreToMaps(result.get("accountinforardet")
										.toString());
								for (Map<Object, Object> map : accountinforardet) {
									map.put("aid_alid", cqid);
								}
								baseDao.execute("delete from al_accountinfordetail where aid_alid=" + cqid + " and aid_kind='应收账款'");
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforardet, "AL_ACCOUNTINFORDETAIL"));
							}
							// 资产情况-其他应收账款
							if (StringUtil.hasText(result.get("accountinforothar"))) {
								List<Map<Object, Object>> accountinforothar = BaseUtil.parseGridStoreToMaps(result.get("accountinforothar")
										.toString());
								for (Map<Object, Object> map : accountinforothar) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforothar, "AL_ACCOUNTINFOR"));
							}
							// 资产情况-预付账款
							if (StringUtil.hasText(result.get("accountinforpp"))) {
								List<Map<Object, Object>> accountinforpp = BaseUtil.parseGridStoreToMaps(result.get("accountinforpp")
										.toString());
								for (Map<Object, Object> map : accountinforpp) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforpp, "AL_ACCOUNTINFOR"));
							}
							// 资产情况-存货
							if (StringUtil.hasText(result.get("accountinforinv"))) {
								List<Map<Object, Object>> accountinforinv = BaseUtil.parseGridStoreToMaps(result.get("accountinforinv")
										.toString());
								for (Map<Object, Object> map : accountinforinv) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforinv, "AL_ACCOUNTINFOR"));
							}
							// 资产情况-固定资产
							if (StringUtil.hasText(result.get("accountinforfix"))) {
								List<Map<Object, Object>> accountinforfix = BaseUtil.parseGridStoreToMaps(result.get("accountinforfix")
										.toString());
								for (Map<Object, Object> map : accountinforfix) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforfix, "AL_ACCOUNTINFOR"));
							}
							// 负责情况-短期借款
							if (StringUtil.hasText(result.get("accountinforlb"))) {
								List<Map<Object, Object>> accountinforlb = BaseUtil.parseGridStoreToMaps(result.get("accountinforlb")
										.toString());
								for (Map<Object, Object> map : accountinforlb) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforlb, "AL_ACCOUNTINFOR"));
							}
							// 负责情况-应付账款
							if (StringUtil.hasText(result.get("accountinforap"))) {
								List<Map<Object, Object>> accountinforap = BaseUtil.parseGridStoreToMaps(result.get("accountinforap")
										.toString());
								for (Map<Object, Object> map : accountinforap) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforap, "AL_ACCOUNTINFOR"));
							}
							// 负责情况-应付账款-债权人明细
							if (StringUtil.hasText(result.get("accountinforapdet"))) {
								List<Map<Object, Object>> accountinforapdet = BaseUtil.parseGridStoreToMaps(result.get("accountinforapdet")
										.toString());
								for (Map<Object, Object> map : accountinforapdet) {
									map.put("aid_alid", cqid);
								}
								baseDao.execute("delete from al_accountinfordetail where aid_alid=" + cqid + " and aid_kind='应付账款'");
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforapdet, "AL_ACCOUNTINFORDETAIL"));
							}
							// 负责情况-其他应付账款
							if (StringUtil.hasText(result.get("accountinforothap"))) {
								List<Map<Object, Object>> accountinforothap = BaseUtil.parseGridStoreToMaps(result.get("accountinforothap")
										.toString());
								for (Map<Object, Object> map : accountinforothap) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforothap, "AL_ACCOUNTINFOR"));
							}
							// 负责情况-长期借款
							if (StringUtil.hasText(result.get("accountinforlong"))) {
								List<Map<Object, Object>> accountinforlong = BaseUtil.parseGridStoreToMaps(result.get("accountinforlong")
										.toString());
								for (Map<Object, Object> map : accountinforlong) {
									map.put("ai_id", baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ"));
									map.put("ai_alid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(accountinforlong, "AL_ACCOUNTINFOR"));
							}
							// 经营情况-前五大上下游客户
							if (StringUtil.hasText(result.get("updowncust"))) {
								List<Map<Object, Object>> updowncust = BaseUtil.parseGridStoreToMaps(result.get("updowncust").toString());
								for (Map<Object, Object> map : updowncust) {
									map.put("udc_id", baseDao.getSeqId("BC_UPDOWNCUST_SEQ"));
									map.put("udc_bcid", cqid);
								}
								baseDao.execute(SqlUtil.getInsertSqlbyGridStore(updowncust, "BC_UPDOWNCUST"));
							}
						}
						updateAssetsLiabilities(cqid);
					} else {
						throw new Exception("连接客户账套失败," + response.getStatusCode());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError("错误：" + e.getMessage());
			}
		}
	}

	void updateAssetsLiabilities(Object cqid) {
		int count = baseDao.getCount("select count(1) from AssetsLiabilities where al_id=" + cqid);
		if (count == 0) {
			baseDao.execute("insert into AssetsLiabilities(AL_ID,AL_CACODE,AL_YEATMONTH) select cq_id,cq_code,to_char(cq_indate,'yyyymm') from CustomerQuota where cq_id="
					+ cqid);
		}
		baseDao.execute("update AssetsLiabilities set al_shortbankloan=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='短期借款-授信银行'),0) where al_id="
				+ cqid);
		baseDao.execute("update AssetsLiabilities set al_shortdebtamount=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='短期借款-贷款银行'),0) where al_id="
				+ cqid);
		baseDao.execute("update AssetsLiabilities set al_shortbalance=nvl(al_shortbankloan,0) + nvl(al_shortdebtamount,0) where al_id="
				+ cqid);
		baseDao.execute("update AssetsLiabilities set al_longbalance=nvl((select sum(ai_leftamount) from AL_ACCOUNTINFOR where ai_alid=al_id and ai_kind='长期借款'),0) where al_id="
				+ cqid);
	}

	@Override
	public void saveSurveyBase(String caller, String formStore, String param1, String param2, String param3, String param4) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("bs_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<Map<Object, Object>> grid4 = BaseUtil.parseGridStoreToMaps(param4);
		List<String> sqls = new ArrayList<String>();

		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("BorrowerSurveyBase", "bs_caid = " + store.get("bs_caid"));
			if (bool) {
				sqls.add(SqlUtil.getInsertSqlByMap(store, "BorrowerSurveyBase"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "BorrowerSurveyBase", "bs_caid"));
			}

			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid1, "FSMFCUSTINFO", "mf_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid2, "BORROWERINVERSTMENT", "bi_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid3, "BORROWERELATION", "br_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid4, "GUARANTEE", "gu_id"));

			baseDao.execute(sqls);

			baseDao.logger.save(caller, "bs_caid", store.get("bs_caid"));
			baseDao.saveClob("BorrowerSurveyBase", clobFields, clobStrs, "bs_caid=" + store.get("bs_caid"));
			baseDao.execute("update FSMFCUSTINFO set MF_STATUS='" + BaseUtil.getLocalMessage("ENTERING")
					+ "',MF_STATUSCODE='ENTERING', MF_RECORDER='" + SystemSession.getUser().getEm_name()
					+ "',mf_indate=sysdate where mf_cqid=" + store.get("bs_caid") + " and nvl(MF_RECORDER,' ')=' '");
			baseDao.execute("update CustomerQuota set cq_quota=nvl((select sum(mf_credit) from FSMFCUSTINFO where mf_cqid=cq_id),0) where cq_id="
					+ store.get("bs_caid") + " and nvl(cq_nomfcust,0)=0");
			baseDao.execute("UPDATE BORROWERSURVEYBASE SET (BS_ACTCONTROLLER,BS_TERM)=(SELECT CU_ACTCONTROLLER,CU_TERM FROM CUSTOMERINFOR,CUSTOMERQUOTA WHERE BS_CAID=CQ_ID AND CQ_CUSTCODE=CU_CODE) WHERE BS_CAID="
					+ store.get("bs_caid"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveCreditStatus(String caller, String formStore, String param1, String param2) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("cc_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<String> sqls = new ArrayList<String>();

		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("CustCreditStatus", "cc_caid = " + store.get("cc_caid"));
			if (bool) {
				sqls.add(SqlUtil.getInsertSqlByMap(store, "CustCreditStatus"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "CustCreditStatus", "cc_caid"));
			}

			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid1, "CUSTOMERBORROWING", "cb_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid2, "CUSTOMERGUARANTY", "cg_id"));

			baseDao.execute(sqls);
			baseDao.saveClob("CustCreditStatus", clobFields, clobStrs, "cc_caid=" + store.get("cc_caid"));
			baseDao.logger.save(caller, "cc_caid", store.get("cc_caid"));

		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveIncomeProfit(String caller, String formStore, String param1, String param2) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("cf_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<String> sqls = new ArrayList<String>();

		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("COSTFEEAUDIT", "cf_caid = " + store.get("cf_caid"));
			if (bool) {
				sqls.add(SqlUtil.getInsertSqlByMap(store, "COSTFEEAUDIT"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "COSTFEEAUDIT", "cf_caid"));
			}

			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid1, "BANKFLOWAUDIT", "bf_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid2, "PAYTAXESAUDIT", "pt_id"));

			baseDao.execute(sqls);
			baseDao.saveClob("COSTFEEAUDIT", clobFields, clobStrs, "cf_caid=" + store.get("cf_caid"));

			baseDao.logger.save(caller, "cf_caid", store.get("cf_caid"));

		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveSurveyConclusion(String caller, String formStore) {

		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("sc_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		String sql = null;

		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("SURVEYCONCLUSION", "sc_caid = " + store.get("sc_caid"));
			if (bool) {
				sql = SqlUtil.getInsertSqlByMap(store, "SURVEYCONCLUSION");
			} else {
				sql = SqlUtil.getUpdateSqlByFormStore(store, "SURVEYCONCLUSION", "sc_caid");
			}

			baseDao.execute(sql);
			baseDao.saveClob("SURVEYCONCLUSION", clobFields, clobStrs, "sc_caid=" + store.get("sc_caid"));
			baseDao.logger.save(caller, "sc_caid", store.get("sc_caid"));

		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveFaReportAnalysis(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("ra_id");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		String sql = null;
		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("FAREPORTANALYSIS", "ra_id = " + store.get("ra_id"));
			if (bool) {
				sql = SqlUtil.getInsertSqlByMap(store, "FAREPORTANALYSIS");
			} else {
				sql = SqlUtil.getUpdateSqlByFormStore(store, "FAREPORTANALYSIS", "ra_id");
			}
			baseDao.execute(sql);
			baseDao.saveClob("FAREPORTANALYSIS", clobFields, clobStrs, "ra_id=" + store.get("ra_id"));
			baseDao.logger.save(caller, "ra_id", store.get("ra_id"));

		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveGuarantee(String caller, String formStore) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("gu_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		String sql = null;
		Object value = null;
		List<String> clobFields = new ArrayList<String>();
		List<String> clobStrs = new ArrayList<String>();
		for (Object field : store.keySet()) {
			value = store.get(field);
			if (value != null) {
				String val = value.toString();
				if (val.length() > 2000) {
					clobFields.add(field.toString());
					clobStrs.add(val);
				}
			}
		}
		boolean bool = baseDao.checkByCondition("Guarantee", "gu_caid = " + store.get("gu_caid"));
		if (bool) {
			sql = SqlUtil.getInsertSqlByMap(store, "Guarantee");
		} else {
			sql = SqlUtil.getUpdateSqlByFormStore(store, "Guarantee", "gu_caid");
		}
		baseDao.execute(sql);
		baseDao.saveClob("Guarantee", clobFields, clobStrs, "gu_caid=" + store.get("gu_caid"));
		baseDao.logger.save(caller, "gu_caid", store.get("gu_caid"));
		baseDao.logger.others("更新担保情况", "更新成功", "CustomerQuota", "cq_id", store.get("gu_caid"));
	}

	@Override
	public void saveMFCustInfo(String gridStore) {
		List<Map<Object, Object>> grid = BaseUtil.parseGridStoreToMaps(gridStore);
		try {
			baseDao.execute(SqlUtil.getInsertOrUpdateSqlbyGridStore(grid, "FSMFCUSTINFO", "mf_id"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

	@Override
	public void saveSurveyBaseZL(String caller, String formStore, String param1, String param2, String param3) {
		Map<Object, Object> store = BaseUtil.parseFormStoreToMap(formStore);
		// 只能对状态为[在录入]的表单进行资料完善!
		Object cq_id = store.get("bs_caid");
		Object status = baseDao.getFieldDataByCondition("CustomerQuota", "cq_statuscode", "cq_id=" + cq_id);
		if (!"ENTERING".equals(status)) {
			BaseUtil.showError("客户额度申请不是【在录入】状态，不允许进行资料完善！");
		}
		List<Map<Object, Object>> grid1 = BaseUtil.parseGridStoreToMaps(param1);
		List<Map<Object, Object>> grid2 = BaseUtil.parseGridStoreToMaps(param2);
		List<Map<Object, Object>> grid3 = BaseUtil.parseGridStoreToMaps(param3);
		List<String> sqls = new ArrayList<String>();

		try {
			Object value = null;
			List<String> clobFields = new ArrayList<String>();
			List<String> clobStrs = new ArrayList<String>();
			for (Object field : store.keySet()) {
				value = store.get(field);
				if (value != null) {
					String val = value.toString();
					if (val.length() > 2000) {
						clobFields.add(field.toString());
						clobStrs.add(val);
					}
				}
			}
			boolean bool = baseDao.checkByCondition("BorrowerSurveyBase", "bs_caid = " + store.get("bs_caid"));
			if (bool) {
				sqls.add(SqlUtil.getInsertSqlByMap(store, "BorrowerSurveyBase"));
			} else {
				sqls.add(SqlUtil.getUpdateSqlByFormStore(store, "BorrowerSurveyBase", "bs_caid"));
			}

			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid1, "FSLEASEITEM", "li_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid2, "FSLEASEDEVICE", "ld_id"));
			sqls.addAll(SqlUtil.getInsertOrUpdateSql(grid3, "GUARANTEE", "gu_id"));
			baseDao.execute(sqls);

			baseDao.logger.save(caller, "bs_caid", store.get("bs_caid"));
			baseDao.saveClob("BorrowerSurveyBase", clobFields, clobStrs, "bs_caid=" + store.get("bs_caid"));
		} catch (Exception e) {
			BaseUtil.showError("保存失败，错误：" + e.getMessage());
		}
	}

}

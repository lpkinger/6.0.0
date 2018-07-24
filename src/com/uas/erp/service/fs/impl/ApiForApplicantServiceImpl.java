package com.uas.erp.service.fs.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.HttpUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.HttpUtil.Response;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SpObserver;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.fs.ApiForApplicantService;

@Service
public class ApiForApplicantServiceImpl implements ApiForApplicantService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Autowired
	private EnterpriseService enterpriseService;
	
	/***
	 * 生成消息
	 * 
	 * **/
	private void beatchNotices(String content, String type, String title, List<Employee> employees, String fromcaller, Object id,
			Object code) {
		List<String> sqls = new ArrayList<String>();
		int pr_id = baseDao.getSeqId("PAGINGRELEASE_SEQ");
		sqls.add("insert into pagingrelease(pr_id,pr_releaser,pr_date,pr_context,PR_FROM,pr_caller,PR_KEYVALUE,PR_CODEVALUE,PR_TITLE,PR_STATUS)values('"
				+ pr_id
				+ "','系统管理员',"
				+ DateUtil.parseDateToOracleString(Constant.YMD, new Date())
				+ ",'"
				+ content
				+ "','"
				+ type
				+ "','"
				+ fromcaller + "'," + id + ",'" + code + "','" + title + "',-1)");
		for (Employee employee : employees) {
			int prd_id = baseDao.getSeqId("PAGINGRELEASEDETAIL_SEQ");
			sqls.add("insert into pagingreleasedetail(prd_id,prd_prid,PRD_RECIPIENTID,PRD_RECIPIENT,PRD_MOBILE,PRD_STATUS,PRD_READSTATUS) values('"
					+ prd_id
					+ "','"
					+ pr_id
					+ "','"
					+ employee.getEm_id()
					+ "','"
					+ employee.getEm_name()
					+ "','"
					+ employee.getEm_mobile() + "',-1,0)");
		}
		// 保存到历史消息表
		int IH_ID = baseDao.getSeqId("ICQHISTORY_SEQ");
		sqls.add("Insert into ICQHISTORY (IH_ID,IH_CALL,IH_CALLID,IH_DATE,IH_CONTEXT,IH_ATTACH,IH_FROM,IH_CODEVALUE,IH_CALLER,IH_TITLE,IH_KEYVALUE,IH_PRID)"
				+ "select "
				+ IH_ID
				+ ",pr_releaser,pr_releaserid,pr_date,pr_context,pr_attach,pr_from,pr_codevalue,pr_caller,pr_title,pr_keyvalue,pr_id from PAGINGRELEASE"
				+ " where pr_id=" + pr_id);
		sqls.add("Insert into ICQHISTORYdetail (IHD_ID,IHD_IHID,IHD_RECEIVE,IHD_RECEIVEID,IHD_MOBILE,IHD_READSTATUS,IHD_STATUS) "
				+ "select ICQHISTORYdetail_seq.nextval," + IH_ID
				+ ",prd_recipient,prd_recipientid,prd_mobile,0,0 from PAGINGRELEASEdetail where prd_prid=" + pr_id);
		baseDao.execute(sqls);
	}
	
	private void callProcedure(List<Integer> yearmonths, String cuvename) throws Exception {
		for (Integer yearmonth : yearmonths) {
			try {
				baseDao.procedure("SP_COUNTFAITEMS_CUST", new Object[] { yearmonth, cuvename });
				baseDao.procedure("SP_COUNTCREDITTARGETSITEMS", new Object[] { yearmonth, cuvename });
			} catch (Exception e) {
				e.printStackTrace();
				BaseUtil.showError(e.getMessage());
			}
		}
	}
	
	@Override
	public Boolean existSecret(String custname) {
		try {
			custname = URLDecoder.decode(custname, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		
		String sob = SpObserver.getSp();
		Master master = enterpriseService.getMasterByName(sob);
		Master parentMaster = null;
		if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (parentMaster!=null) {
			SpObserver.putSp(parentMaster.getMa_name());
		}else{
			SpObserver.putSp(sob);
		}
		
		return baseDao.checkIf("CustomerInfor", "cu_name = '" + custname + "' and nvl(cu_secret,' ') <>' '");
	}

	@Override
	public Map<String, Object> assessFinancingApply(String FinancingApply, Integer year, String yearmonths) {
		
		String sob = SpObserver.getSp();
		Master master = enterpriseService.getMasterByName(sob);
		Master parentMaster = null;
		if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (parentMaster!=null) {
			SpObserver.putSp(parentMaster.getMa_name());
		}else{
			SpObserver.putSp(sob);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		try {
			FinancingApply = URLDecoder.decode(FinancingApply, "utf-8");
			if (yearmonths != null) {
				yearmonths = URLDecoder.decode(yearmonths, "utf-8");
			}
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}

		Map<Object, Object> financingApply = BaseUtil.parseFormStoreToMap(FinancingApply);

		try {

			Integer orgId = baseDao.queryForObject("select or_id from HrOrg where or_name = '企业金融部' or or_department = '企业金融部'",
					Integer.class);
			List<Employee> employees = null;
			// 先生成客户信息
			Map<Object, Object> custInfo = new HashMap<Object, Object>();
			custInfo.put("cu_issys", -1);

			if (StringUtil.hasText(financingApply.get("en_erpurl"))) {
				custInfo.put("cu_webserver", financingApply.get("en_erpurl"));
			}

			if (StringUtil.hasText(financingApply.get("en_whichsystem"))) {
				custInfo.put("cu_whichsystem", financingApply.get("en_whichsystem"));
			}

			custInfo.put("cu_contact", financingApply.get("fa_contact"));
			custInfo.put("cu_contactphone", financingApply.get("fa_phone"));
			if (StringUtil.hasText(financingApply.get("en_finsecret"))) {
				custInfo.put("cu_secret", financingApply.get("en_finsecret"));
			}

			Object cu_code = null;
			Object cuid = null;
			Object custname = financingApply.get("fa_enname");
			Object[] cu = baseDao.getFieldsDataByCondition("CustomerInfor", "cu_id,cu_code,cu_accmgncode", "cu_name = '" + custname + "'");

			if (cu == null) {
				cu_code = baseDao.sGetMaxNumber("CustomerInfor", 2);
				cuid = baseDao.getSeqId("CUSTOMERINFOR_SEQ");
				custInfo.put("cu_id", cuid);
				custInfo.put("cu_code", cu_code);
				custInfo.put("cu_name", custname);
				custInfo.put("cu_engname", financingApply.get("en_name_en"));
				custInfo.put("cu_date", DateUtil.format(new Date(), Constant.YMD));
				custInfo.put("cu_capcurrency", financingApply.get("capcurrency"));
				custInfo.put("cu_regcapital", financingApply.get("en_registercapital"));
				custInfo.put("cu_regadd", financingApply.get("en_address"));
				custInfo.put("cu_officeadd", financingApply.get("fa_addr"));
				custInfo.put("cu_nationtax", financingApply.get("en_taxcode"));
				custInfo.put("cu_landtax", financingApply.get("en_landtaxcode"));
				custInfo.put("cu_businesscode", financingApply.get("en_businesscode"));
				custInfo.put("cu_enterptype", financingApply.get("en_type"));
				custInfo.put("cu_corporation", financingApply.get("en_corporation"));
				custInfo.put("cu_status", BaseUtil.getLocalMessage("ENTERING"));
				custInfo.put("cu_statuscode", "ENTERING");
				baseDao.execute(SqlUtil.getInsertSqlByMap(custInfo, "CustomerInfor"));
				if (orgId != null) {
					employees = baseDao
							.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_mobile FROM employee where (em_defaultorid=? or em_id "
									+ "in (select emp_id from empsjobs where org_id=?) or em_code = (select or_headmancode from HrOrg where or_id = ?)) and NVL(em_class,' ')<>'离职'",
									Employee.class, orgId, orgId, orgId);
					if (employees != null) {
						String context = "<font color=\"#0000ff\">有新用户加入,<a href=\"javascript:openUrl(''jsps/fs/cust/customerInfor.jsp?formCondition=cu_idIS"
								+ cuid
								+ "&gridCondition=ce_cuidIS"
								+ cuid
								+ "&_noc=1&datalistId=NaN'')\" style=\"font-size:14px; color:blue;\">" + custname + "</a></font>";
						beatchNotices(context, "system", "金融知会消息", employees, "Customer!Infor", cuid, cu_code);
					}
				}
			} else {
				cuid = cu[0];
				custInfo.put("cu_id", cu[0]);
				cu_code = cu[1];
				baseDao.execute(SqlUtil.getUpdateSqlByFormStore(custInfo, "CustomerInfor", "cu_id"));
				if (orgId != null) {
					String orgheadcode = baseDao.queryForObject("select or_headmancode from HrOrg where or_id = ?", String.class, orgId);
					employees = baseDao.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_mobile FROM employee where "
							+ "em_code in ('" + orgheadcode + "','" + cu[2] + "') and NVL(em_class,' ')<>'离职'", Employee.class);
				}
			}
			int count = baseDao.getCount("select count(1) from FSCHANGESINSTRUCTION  where cs_cuid=" + cuid);
			if (count == 0) {
				List<String> sqls = new ArrayList<String>();
				sqls.add("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(" 
						+ baseDao.getSeqId("FSCHANGESINSTRUCTION_SEQ") + ", " + cuid + ", 1, '股东')");
				sqls.add("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(" 
						+ baseDao.getSeqId("FSCHANGESINSTRUCTION_SEQ") + ", " + cuid + ", 2, '法人')");
				sqls.add("insert into FSCHANGESINSTRUCTION (CS_ID,CS_CUID,CD_DETNO,CD_TYPE) values(" 
						+ baseDao.getSeqId("FSCHANGESINSTRUCTION_SEQ") + ", " + cuid + ", 3, '住所')");
				
				baseDao.execute(sqls);
			}

			Object creditdate = baseDao.getFieldDataByCondition("CUSTCREDITRATINGAPPLY", "cra_auditdate", "cra_cuvename = '" + custname
					+ "' and cra_valid = 'VALID' and cra_type = '信用评级申请'");
			if (creditdate == null) {
				// 生成信用评级申请单
				Map<Object, Object> custCreditRatingApply = new HashMap<Object, Object>();
				int cra_id = baseDao.getSeqId("CUSTCREDITRATINGAPPLY_SEQ");
				custCreditRatingApply.put("cra_id", cra_id);
				custCreditRatingApply.put("cra_type", "信用评级申请");
				custCreditRatingApply.put("cra_code", baseDao.sGetMaxNumber("CUSTCREDITRATINGAPPLY", 2));
				custCreditRatingApply.put("cra_applyman", financingApply.get("fa_applyman"));
				custCreditRatingApply.put("cra_date", financingApply.get("fa_applydate"));
				custCreditRatingApply.put("cra_status", BaseUtil.getLocalMessage("ENTERING"));
				custCreditRatingApply.put("cra_issyscust", 0);
				custCreditRatingApply.put("cra_cuvecode", financingApply.get("en_uu"));
				custCreditRatingApply.put("cra_cuvename", custname);
				custCreditRatingApply.put("cra_yearmonth", year);
				custCreditRatingApply.put("cra_statuscode", "ENTERING");
				custCreditRatingApply.put("cra_indate", DateUtil.format(new Date(), Constant.YMD));
				baseDao.execute(SqlUtil.getInsertSqlByMap(custCreditRatingApply, "CustCreditRatingApply"));
			}

			boolean bool = baseDao.checkByCondition("CUSTCREDITRATINGAPPLY", "cra_cuvename = '" + custname
					+ "' and cra_valid = 'VALID' and cra_type = '企业信用风险'");
			if (bool) {
				// 生成企业信用风险
				Map<Object, Object> custCreditRatingApply = new HashMap<Object, Object>();
				int cra_id = baseDao.getSeqId("CUSTCREDITRATINGAPPLY_SEQ");
				custCreditRatingApply.put("cra_id", cra_id);
				custCreditRatingApply.put("cra_type", "企业信用风险");
				custCreditRatingApply.put("cra_code", baseDao.sGetMaxNumber("CUSTCREDITRATINGAPPLY", 2));
				custCreditRatingApply.put("cra_applyman", financingApply.get("fa_applyman"));
				custCreditRatingApply.put("cra_date", financingApply.get("fa_applydate"));
				custCreditRatingApply.put("cra_status", BaseUtil.getLocalMessage("ENTERING"));
				custCreditRatingApply.put("cra_issyscust", 0);
				custCreditRatingApply.put("cra_cuvecode", financingApply.get("en_uu"));
				custCreditRatingApply.put("cra_cuvename", custname);
				custCreditRatingApply.put("cra_statuscode", "ENTERING");
				custCreditRatingApply.put("cra_indate", DateUtil.format(new Date(), Constant.YMD));
				baseDao.execute(SqlUtil.getInsertSqlByMap(custCreditRatingApply, "CustCreditRatingApply"));
			}
			SqlRowList rs = baseDao.queryForRowSet(
					"select cu_webserver,cu_whichsystem,cu_secret FROM  CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0", cu_code);
			if (rs.next()) {
				String website = rs.getGeneralString("cu_webserver");
				String whichsystem = rs.getGeneralString("cu_whichsystem");
				String secret = rs.getGeneralString("cu_secret");
				if (StringUtil.hasText(website) && StringUtil.hasText(whichsystem) && StringUtil.hasText(secret)) {
					
					Map<String, String> params = new HashMap<String, String>();
					List<Integer> yearmonths1 = new ArrayList<Integer>();
					if (yearmonths != null) {
						List<Integer> YearMonths = FlexJsonUtil.fromJsonArray(yearmonths, Integer.class);
						
						for (Integer yearmonth : YearMonths) {
							boolean existFaReports = baseDao.checkIf("CustFAReport", "cr_cuname='" + custname + "' and cr_yearmonth = "
									+ yearmonth + " and cr_fatype <> '其他项目'");
							if (!existFaReports) {
								yearmonths1.add(yearmonth);
							}
						}
					}
				
					params.put("yearmonths", FlexJsonUtil.toJsonArray(yearmonths1));
					boolean exitUDStream = baseDao.checkIf("CUSTOMERUDSTREAM LEFT JOIN CUSTOMERINFOR ON　CUD_CUID＝CU_ID", "cu_name = '"
							+ custname + "'");
					params.put("exitUDStream", String.valueOf(exitUDStream));
					params.put("right", String.valueOf(true));
					
					Response response = HttpUtil.sendPostRequest(website+"/openapi/factoring/getFaReports.action?master="+whichsystem, params, true, secret);
					if (response.getStatusCode() == HttpStatus.OK.value()) {
						String data = response.getResponseText();
						if (StringUtil.hasText(data)) {
							Map<Object, Object> datas = BaseUtil.parseFormStoreToMap(data);
							List<String> sqls = new ArrayList<String>();
							Object CustUDStream = datas.get("CustUDStream");
							// 保存上下游关联方情况
							if (CustUDStream != null) {
								List<Map<Object, Object>> custUDStream = BaseUtil.parseGridStoreToMaps(CustUDStream.toString());
								for (int i = 0; i < custUDStream.size(); i++) {
									custUDStream.get(i).put("cud_id", baseDao.getSeqId("CUSTOMERUDSTREAM_SEQ"));
									custUDStream.get(i).put("cud_cuid", cuid);
									custUDStream.get(i).put("cud_detno", i + 1);
								}
								sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custUDStream, "CUSTOMERUDSTREAM"));
							}
	
							Object CustFaReportss = datas.get("CustFaReportss");
							// 复制财务报表
							List<String> custFaReportss = FlexJsonUtil.fromJsonArray(CustFaReportss.toString(), String.class);
							for (String CustFaReports : custFaReportss) {
								List<String> custFaReports = FlexJsonUtil.fromJsonArray(CustFaReports, String.class);
								for (int i = 0; i < custFaReports.size(); i++) {
									String str = custFaReports.get(i);
									List<Map<Object, Object>> custFaReportDetail = BaseUtil.parseGridStoreToMaps(str);
									if (custFaReportDetail.size() > 0) {
										Map<Object, Object> custFaReport = new HashMap<Object, Object>();
										int cr_id = baseDao.getSeqId("CUSTFAREPORT_SEQ");
										custFaReport.put("cr_id", cr_id);
										custFaReport.put("cr_date", custFaReportDetail.get(0).get("crd_indate"));
										custFaReport.put("cr_fatype", custFaReportDetail.get(0).get("crd_fsname"));
										custFaReport.put("cr_cuname", custname);
										custFaReport.put("cr_yearmonth", custFaReportDetail.get(0).get("crd_yearmonth"));
										custFaReport.put("cr_cucode", cu_code);
										sqls.add(SqlUtil.getInsertSqlByMap(custFaReport, "CustFAReport"));
	
										for (Map<Object, Object> map : custFaReportDetail) {
											int crd_id = baseDao.getSeqId("CUSTFAREPORTDETAIL_SEQ");
											map.put("crd_id", crd_id);
											map.put("crd_crid", cr_id);
										}
										sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custFaReportDetail, "CustFAReportDetail"));
									}
								}
							}
	
							// 复制其他报表
							Object CustOthReports = datas.get("CustOthReports");
							List<String> custOthReports = FlexJsonUtil.fromJsonArray(CustOthReports.toString(), String.class);
							for (int i = 0; i < custOthReports.size(); i++) {
								String str = custOthReports.get(i);
								List<Map<Object, Object>> custOthReportDetail = BaseUtil.parseGridStoreToMaps(str);
								if (custOthReportDetail.size() > 0) {
									Map<Object, Object> custFaReport = new HashMap<Object, Object>();
									Object type = custOthReportDetail.get(0).get("crd_fsname");
									custFaReport.put("cr_date", custOthReportDetail.get(0).get("crd_indate"));
									custFaReport.put("cr_fatype", custOthReportDetail.get(0).get("crd_fsname"));
									custFaReport.put("cr_cuname", custname);
									custFaReport.put("cr_yearmonth", custOthReportDetail.get(0).get("crd_yearmonth"));
									custFaReport.put("cr_cucode", cu_code);
	
									Object cr_id = baseDao.getFieldDataByCondition("CustFAReport", "cr_id", "cr_fatype = '" + type
											+ "' and cr_cucode = '" + cu_code + "'");
	
									if (StringUtil.hasText(cr_id)) {
										custFaReport.put("cr_id", cr_id);
										sqls.add(SqlUtil.getUpdateSqlByFormStore(custFaReport, "CustFAReport", "cr_id"));
										baseDao.deleteByCondition("CustFAReportDetail", "crd_crid = ?", cr_id);
									} else {
										cr_id = baseDao.getSeqId("CUSTFAREPORT_SEQ");
										custFaReport.put("cr_id", cr_id);
										sqls.add(SqlUtil.getInsertSqlByMap(custFaReport, "CustFAReport"));
									}
	
									for (Map<Object, Object> map : custOthReportDetail) {
										int crd_id = baseDao.getSeqId("CUSTFAREPORTDETAIL_SEQ");
										map.put("crd_id", crd_id);
										map.put("crd_crid", cr_id);
									}
									sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custOthReportDetail, "CustFAReportDetail"));
								}
							}
							baseDao.execute(sqls);
							// 计算申请得分
							callProcedure(yearmonths1, String.valueOf(custname));
						}
					} else {
						throw new RuntimeException("连接客户账套失败," + response.getStatusCode());
					}
				}
			}

			// 生成保理额度申请单
			List<String> sqls = new ArrayList<String>();
			Map<Object, Object> customerQuota = new HashMap<Object, Object>();
			Integer cqid = baseDao.getSeqId("CUSTOMERQUOTA_SEQ");
			String cqcode = baseDao.sGetMaxNumber("CUSTOMERQUOTA", 2);

			customerQuota.put("cq_id", cqid);
			customerQuota.put("cq_code", cqcode);
			customerQuota.put("cq_custcode", cu_code);
			customerQuota.put("cq_custname", custname);
			customerQuota.put("cq_currency", financingApply.get("capcurrency"));
			customerQuota.put("cq_applyquota", financingApply.get("fa_appamount"));
			customerQuota.put("cq_quotatype", "正向保理业务");
			customerQuota.put("cq_type", "公开型有追索权国内保理");
			customerQuota.put("cq_status", BaseUtil.getLocalMessage("ENTERING"));
			customerQuota.put("cq_statuscode", "ENTERING");
			customerQuota.put("cq_indate", DateUtil.format(new Date(), Constant.YMD));
			customerQuota.put("cq_finid", financingApply.get("fa_finid"));

			sqls.add(SqlUtil.getInsertSqlByMap(customerQuota, "CUSTOMERQUOTA"));

			// 生成买方客户资料
			String buyer = StringUtil.nvl(financingApply.get("fa_buyer"), "");
			String buyercode = StringUtil.nvl(financingApply.get("fa_buyercode"), "");
			if (buyer.length() > 0 && buyercode.length() > 0) {
				String[] buyercodes = buyercode.split("；");
				String[] buyers = buyer.split("；");
				for (int i = 0; i < buyercodes.length; i++) {
					Object[] custinfor = baseDao.getFieldsDataByCondition("CustomerInfor",
							"cu_code,cu_corporation,cu_regcapital,cu_officeadd,cu_licensedate,cu_contact,cu_contactnum,cu_businsscope",
							"cu_name = '" + buyers[i] + "'");
					String custcode = null;
					if (custinfor == null) {
						int custid = baseDao.getSeqId("CUSTOMERINFOR_SEQ");
						custcode = baseDao.sGetMaxNumber("CUSTOMERINFOR", 2);
						Map<String, Object> cust = new HashMap<String, Object>();
						cust.put("cu_id", custid);
						cust.put("cu_code", custcode);
						cust.put("cu_name", buyers[i]);
						cust.put("cu_date", DateUtil.getCurrentDate());
						cust.put("cu_status", BaseUtil.getLocalMessage("ENTERING"));
						cust.put("cu_statuscode", "ENTERING");
						sqls.add(SqlUtil.getInsertSqlByMap(cust, "CUSTOMERINFOR"));
					} else {
						custcode = String.valueOf(custinfor[0]);
					}
					int mfid = baseDao.getSeqId("FSMFCUSTINFO_SEQ");
					Map<String, Object> mfcust = new HashMap<String, Object>();
					mfcust.put("mf_id", mfid);
					mfcust.put("mf_cqid", cqid);
					mfcust.put("mf_detno", i + 1);
					mfcust.put("mf_custcode", custcode);
					mfcust.put("mf_custname", buyers[i]);
					if (custinfor != null) {
						mfcust.put("mf_legrep", StringUtil.nvl(custinfor[1], ""));
						mfcust.put("mf_regcapital", custinfor[2] == null ? 0 : custinfor[2]);
						mfcust.put("mf_addr", StringUtil.nvl(custinfor[3], ""));
						mfcust.put("mf_estabtime", StringUtil.nvl(custinfor[4], ""));
						mfcust.put("mf_contact", StringUtil.nvl(custinfor[5], ""));
						mfcust.put("mf_contactnum", StringUtil.nvl(custinfor[6], ""));
						mfcust.put("mf_businsscope", StringUtil.nvl(custinfor[7], ""));
					}

					mfcust.put("mf_indate", DateUtil.getCurrentDate());
					mfcust.put("mf_status", BaseUtil.getLocalMessage("ENTERING"));
					mfcust.put("mf_statuscode", "ENTERING");
					mfcust.put("mf_sourcecode", buyercodes[i]);
					sqls.add(SqlUtil.getInsertSqlByMap(mfcust, "FSMFCUSTINFO"));
				}
			}
			baseDao.execute(sqls);
			result.put("busincode", cqcode);
			// 存储改错
			baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { cqid, "保理额度申请" });

			if (employees != null) {
				String context = "<font color=\"#0000ff\"><a href=\"javascript:openUrl(''jsps/fs/cust/customerQuota.jsp?formCondition=cq_idIS"
						+ cqid
						+ "&_noc=1&datalistId=NaN'')\" style=\"font-size:14px; color:blue;\">"
						+ custname
						+ "</a></font>有新增申请";
				beatchNotices(context, "system", "金融知会消息", employees, "CustomerQuota", cqid, cqcode);
			}

			// 生成申请进度
			Map<Object, Object> finapply = new HashMap<Object, Object>();
			int fsid = baseDao.getSeqId("FINBUSINAPPLY_SEQ");
			String now = DateUtil.format(new Date(), Constant.YMD_HMS);
			finapply.put("fs_id", fsid);
			finapply.put("fs_cqcode", cqcode);
			finapply.put("fs_custcode", cu_code);
			finapply.put("fs_custname", custname);
			finapply.put("fs_applydate", now);
			finapply.put("fs_crdratdate", StringUtil.hasText(creditdate) ? now : "");
			if (StringUtil.hasText(creditdate)) {
				finapply.put("fs_status", "应收账款转让");
			} else {
				finapply.put("fs_status", "收集材料");
			}

			baseDao.execute(SqlUtil.getInsertSqlByMap(finapply, "FINBUSINAPPLY"));
			if (parentMaster!=null) {
				SpObserver.putSp(sob);
				baseDao.execute(SqlUtil.getInsertSqlByMap(finapply, "FINBUSINAPPLY"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}
	
	private boolean isEmpty(Object obj){
		return !StringUtil.hasText(obj)||"null".equals(obj.toString());
	}
	
	private List<String> saveAttaches(List<Map<Object, Object>> Attaches, Object formid, String caller, Object operator){

		List<String> sqls = new ArrayList<String>();
		Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(Attaches, new Object[] { "type"});
		
		boolean noExist = baseDao.checkByCondition("formsdoc", "fd_kind=-1 and fd_caller='"+caller+"' and fd_formsid=" + formid);
		if (noExist) {
			String filestemp = baseDao.getFieldValue("form", "fo_filestemp", "fo_caller='"+caller+"'",String.class);
			if (StringUtil.hasText(filestemp)) {
				String code = baseDao.sGetMaxNumber("FORMSDOC",2);
				baseDao.execute("insert into formsdoc (fd_formsid,fd_id,fd_kind,fd_parentid,fd_name,fd_remark,fd_virtualpath,fd_detno,fd_doccode,fd_caller,fd_tempid) select "
						+ formid
						+ ",formsdoc_seq.nextval,kind_,parentid_,name_,remark_,virtualpath_,detno_,"+code
						+",'"
						+ caller
						+ "',id_ from PRJDOC_TEMP where PRJTYPECODE_='"
						+ filestemp.toString()
						+ "'");
				
				baseDao.execute("update formsdoc a set a.fd_parentid=(select b.fd_id from formsdoc b,PRJDOC_TEMP where b.fd_tempid=id_ "
						+ "and id_=a.fd_parentid and a.fd_formsid=b.fd_formsid and a.fd_caller=b.fd_caller) where a.fd_parentid<>0 "
						+ "and a.fd_formsid=" + formid + " and fd_caller='" + caller + "'");
			}
		}
		for (Object m : map.keySet()) {
			Object [] pNode = baseDao.getFieldsDataByCondition("FORMSDOC", new String[]{"fd_id","fd_virtualpath"}, "fd_kind=-1 and "
					+ "fd_caller='"+caller+"' and fd_name = '"+m+"' and fd_formsid= "+formid);
			
			if (pNode!=null) {
				List<Map<Object, Object>> attaches = map.get(m);
				if (!CollectionUtils.isEmpty(attaches)) {
					List<Map<Object, Object>> files = new ArrayList<Map<Object,Object>>();
					List<Map<Object, Object>> nodes = new ArrayList<Map<Object,Object>>();
					int detno = 0;
					for (Map<Object, Object> attache : attaches) {
						Map<Object, Object> node = new HashMap<Object, Object>();
						Map<Object, Object> file = new HashMap<Object, Object>();
						node.put("fd_formsid", formid);
						int id = baseDao.getSeqId("FORMSDOC_SEQ");
						String code = baseDao.sGetMaxNumber("FORMSDOC",2);
						node.put("fd_id", id);
						node.put("fd_kind", 0);
						node.put("fd_parentid", pNode[0]);
						Object name = attache.get("name");
						if (name!=null) {
							String Name = String.valueOf(name).substring(0,String.valueOf(name).indexOf("."));
							String virtualpath = pNode[1]+"/"+Name;
							node.put("fd_name", Name);
							node.put("fd_virtualpath", virtualpath);
						}
						String now = DateUtil.format(new Date(), Constant.YMD_HMS);
						node.put("fd_operatime", now);
						node.put("fd_operator", operator);
						int fpid = baseDao.getSeqId("EMAILFILEPATH");
						file.put("fp_id", fpid);
						file.put("fp_path", attache.get("path"));
						file.put("fp_size", attache.get("size"));
						file.put("fp_man", operator);
						file.put("fp_date", now);
						file.put("fp_name", name);
						files.add(file);
						node.put("fd_filepath", name+";"+fpid);
						node.put("fd_doccode", code);
						node.put("fd_detno", ++detno);
						node.put("fd_caller", caller);
						nodes.add(node);
					}
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(files, "FILEPATH"));
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(nodes, "FORMSDOC"));
				}
			}
		}
		return sqls;
	}
	
	
	@Override
	public Map<String, Object> financingApply(String FinancingApply, String customer, String attacheList, String customerExcutive, String shareholders, 
			String AssociateCompany, String changeInstruction, String mfCust, String businessCondition, String prouductMixe, 
			String upDownCast, String FinanceCondition, String accountList) {
		
		String sob = SpObserver.getSp();
		Master master = enterpriseService.getMasterByName(sob);
		Master parentMaster = null;
		if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (parentMaster!=null) {
			SpObserver.putSp(parentMaster.getMa_name());
		}else{
			SpObserver.putSp(sob);
		}
		
		Map<String, Object> result = new HashMap<String, Object>();
		
		FinancingApply = isEmpty(FinancingApply)?null:FinancingApply;
		customer = isEmpty(customer)?null:customer;
		attacheList = isEmpty(attacheList)?"[]":attacheList;
		customerExcutive = isEmpty(customerExcutive)?"[]":customerExcutive;
		shareholders = isEmpty(shareholders)?"[]":shareholders;
		AssociateCompany = isEmpty(AssociateCompany)?"[]":AssociateCompany;
		changeInstruction = isEmpty(changeInstruction)?"[]":changeInstruction;
		mfCust = isEmpty(mfCust)?"[]":mfCust;
		businessCondition = isEmpty(businessCondition)?null:businessCondition;
		prouductMixe = isEmpty(prouductMixe)?"[]":prouductMixe;
		upDownCast = isEmpty(upDownCast)?"[]":upDownCast;
		FinanceCondition = isEmpty(FinanceCondition)?null:FinanceCondition;
		accountList = isEmpty(accountList)?"[]":accountList;
		
		try {
			if (null==FinancingApply) {
				BaseUtil.showError("未发起保理融资申请！");
			}
			
			Map<Object, Object> financingApply = BaseUtil.parseFormStoreToMap(FinancingApply);
			Object b2bid = financingApply.get("id");
			Object cqcode = baseDao.getFieldDataByCondition("CUSTOMERQUOTA", "cq_code", "cq_finid = "+b2bid);
			if (cqcode!=null) {
				result.put("busincode", cqcode);
				return result;
			}
			
			Integer orgId = baseDao.queryForObject("select or_id from HrOrg where or_name = '企业金融部' or or_department = '企业金融部'",
					Integer.class);
			List<Employee> employees = null;
			// 先生成客户信息
			Map<Object, Object> custInfo = BaseUtil.parseFormStoreToMap(customer);
			if (null==custInfo) {
				custInfo = new HashMap<Object, Object>();
			}
			
			if (StringUtil.hasText(financingApply.get("fa_contact"))) {
				custInfo.put("cu_contact", financingApply.get("fa_contact"));
			}
			if (StringUtil.hasText(financingApply.get("fa_telphone"))) {
				custInfo.put("cu_contactnum", financingApply.get("fa_telphone"));
			}
			if (StringUtil.hasText(financingApply.get("fa_phone"))) {
				custInfo.put("cu_contactphone", financingApply.get("fa_phone"));
			}
			
			custInfo.put("cu_b2benable", -1);

			if (!StringUtil.hasText(financingApply.get("fa_hasErp")) || Integer.parseInt(financingApply.get("fa_hasErp").toString()) == 0) {
				custInfo.put("cu_issys",0);
			} else {
				custInfo.put("cu_issys", -1);
			}

			Object cu_code = null;
			Object cuid = null;
			Object custname = StringUtil.hasText(custInfo.get("cu_name"))?custInfo.get("cu_name"):financingApply.get("fa_enname");
			Object[] cu = baseDao.getFieldsDataByCondition("CustomerInfor", "cu_id,cu_code,cu_accmgncode", "cu_name = '" + custname + "'");
			List<String> sqls = new ArrayList<String>();
			if (null==cu&&null!=customer) {
				cu_code = baseDao.sGetMaxNumber("CustomerInfor", 2);
				cuid = baseDao.getSeqId("CUSTOMERINFOR_SEQ");
				custInfo.put("cu_id", cuid);
				custInfo.put("cu_code", cu_code);
				custInfo.put("cu_name", custname);
				custInfo.put("cu_status", BaseUtil.getLocalMessage("ENTERING"));
				custInfo.put("cu_statuscode", "ENTERING");
				sqls.add(SqlUtil.getInsertSqlByMap(custInfo, "CustomerInfor"));
				if (orgId != null) {
					employees = baseDao
							.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_mobile FROM employee where (em_defaultorid=? or em_id "
									+ "in (select emp_id from empsjobs where org_id=?) or em_code = (select or_headmancode from HrOrg where or_id = ?)) and NVL(em_class,' ')<>'离职'",
									Employee.class, orgId, orgId, orgId);
					if (employees != null) {
						String context = "<font color=\"#0000ff\">有新用户加入,<a href=\"javascript:openUrl(''jsps/fs/cust/customerInfor.jsp?formCondition=cu_idIS"
								+ cuid
								+ "&gridCondition=ce_cuidIS"
								+ cuid
								+ "&_noc=1&datalistId=NaN'')\" style=\"font-size:14px; color:blue;\">" + custname + "</a></font>";
						beatchNotices(context, "system", "金融知会消息", employees, "Customer!Infor", cuid, cu_code);
					}
				}
			} else {
				cuid = cu[0];
				custInfo.put("cu_id", cu[0]);
				cu_code = cu[1];
				sqls.add(SqlUtil.getUpdateSqlByFormStore(custInfo, "CustomerInfor", "cu_id"));
				if (orgId != null) {
					String orgheadcode = baseDao.queryForObject("select or_headmancode from HrOrg where or_id = ?", String.class, orgId);
					employees = baseDao.query("SELECT distinct em_id,em_code,em_name,em_defaulthsid,em_mobile FROM employee where "
							+ "em_code in ('" + orgheadcode + "','" + cu[2] + "') and NVL(em_class,' ')<>'离职'", Employee.class);
				}
			}
			if (null!=cuid) {
				//附件管理
				List<Map<Object, Object>> Attaches = BaseUtil.parseGridStoreToMaps(attacheList);
				if (!CollectionUtils.isEmpty(Attaches)) {
					sqls.addAll(saveAttaches(Attaches, cuid, "Customer!Infor", financingApply.get("fa_applyman")));
				}
				
				List<Map<Object, Object>> custPersonInfos = new ArrayList<Map<Object,Object>>();  
				//高管信息
				List<Map<Object, Object>> excutiveInfos = BaseUtil.parseGridStoreToMaps(customerExcutive);
				if (!CollectionUtils.isEmpty(excutiveInfos)) {
					int detno = 0;
					for (Map<Object, Object> excutive : excutiveInfos) {
						excutive.remove("enuu");
						int ceid = baseDao.getSeqId("CUSTOMEREXCUTIVE_SEQ");
						excutive.put("ce_id", ceid);
						excutive.put("ce_cuid", cuid);
						excutive.put("ce_detno", ++detno);
						
						//客户个人信息
						Map<Object, Object> custPerson = new HashMap<Object, Object>();
						custPerson.put("cp_id", baseDao.getSeqId("CUSTPERSONINFO_SEQ"));
						custPerson.put("cp_custid", cuid);
						custPerson.put("cp_custcode", cu_code);
						custPerson.put("cp_custname", custname);
						custPerson.put("cp_name", excutive.get("ce_name"));
						custPerson.put("cp_othname", excutive.get("ce_othname"));
						custPerson.put("cp_papertype", excutive.get("ce_paperstype"));
						custPerson.put("cp_papercode", excutive.get("ce_paperscode"));
						custPerson.put("cp_sex", excutive.get("ce_sex"));
						custPerson.put("cp_birthdate", excutive.get("ce_birthday"));
						custPerson.put("cp_nationality", excutive.get("ce_nationality"));
						custPerson.put("cp_censusreg", excutive.get("ce_register"));
						custPerson.put("cp_livingadd", excutive.get("ce_nowaddress"));
						custPerson.put("cp_contactway", excutive.get("ce_contactnum"));
						custPerson.put("cp_education", excutive.get("ce_education"));
						custPerson.put("cp_status", BaseUtil.getLocalMessage("ENTERING"));
						custPerson.put("cp_statuscode", "ENTERING");
						custPerson.put("cp_indate", DateUtil.getCurrentDate());
						custPersonInfos.add(custPerson);
					}
					
					sqls.add("delete from CUSTOMEREXCUTIVE where ce_cuid = "+cuid);
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(excutiveInfos, "CUSTOMEREXCUTIVE"));
				}
				
				//股东信息
				List<Map<Object, Object>> shareholdersInfos = BaseUtil.parseGridStoreToMaps(shareholders);
				if (!CollectionUtils.isEmpty(shareholdersInfos)) {
					int detno = 0;
					for (Map<Object, Object> shareholder : shareholdersInfos) {
						shareholder.remove("enuu");
						int csid = baseDao.getSeqId("CUSTOMERSHAREHOLDER_SEQ");
						shareholder.put("cs_id", csid);
						shareholder.put("cs_cuid", cuid);
						shareholder.put("cs_detno", ++detno);
						
						//客户个人信息
						Map<Object, Object> custPerson = new HashMap<Object, Object>();
						custPerson.put("cp_id", baseDao.getSeqId("CUSTPERSONINFO_SEQ"));
						custPerson.put("cp_custid", cuid);
						custPerson.put("cp_custcode", cu_code);
						custPerson.put("cp_custname", custname);
						custPerson.put("cp_name", shareholder.get("cs_name"));
						custPerson.put("cp_papertype", shareholder.get("cs_paperstype"));
						custPerson.put("cp_papercode", shareholder.get("cs_paperscode"));
						custPerson.put("cp_status", BaseUtil.getLocalMessage("ENTERING"));
						custPerson.put("cp_statuscode", "ENTERING");
						custPerson.put("cp_indate", DateUtil.getCurrentDate());
						custPersonInfos.add(custPerson);
					}
					sqls.add("delete from CUSTOMERSHAREHOLDER where cs_cuid = "+cuid);
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(shareholdersInfos, "CUSTOMERSHAREHOLDER"));
				}
				
				//客户个人信息
				if (!CollectionUtils.isEmpty(custPersonInfos)) {
					sqls.add("delete from CUSTPERSONINFO where cp_custid = "+cuid);
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custPersonInfos, "CUSTPERSONINFO"));
				}
				
				// 关联企业
				List<Map<Object, Object>> associateCompanyInfos = BaseUtil.parseGridStoreToMaps(AssociateCompany);
				if (!CollectionUtils.isEmpty(associateCompanyInfos)) {
					int detno = 0;
					for (Map<Object, Object> associateCompany : associateCompanyInfos) {
						associateCompany.remove("enuu");
						int cudid = baseDao.getSeqId("CUSTOMERUDSTREAM_SEQ");
						associateCompany.put("cud_id", cudid);
						associateCompany.put("cud_cuid", cuid);
						associateCompany.put("cud_detno", ++detno);
					}
					sqls.add("delete from CUSTOMERUDSTREAM where cud_cuid = "+cuid);
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(associateCompanyInfos, "CUSTOMERUDSTREAM"));
				}
				
				//变更说明
				List<Map<Object, Object>> changesInstructionInfos = BaseUtil.parseGridStoreToMaps(changeInstruction);
				if (!CollectionUtils.isEmpty(changesInstructionInfos)) {
					int detno = 0;
					for (Map<Object, Object> changesInstruction : changesInstructionInfos) {
						changesInstruction.remove("enuu");
						int csid = baseDao.getSeqId("FSCHANGESINSTRUCTION_SEQ");
						changesInstruction.put("cs_id", csid);
						changesInstruction.put("cs_cuid", cuid);
						changesInstruction.put("cd_detno", ++detno);
					}
					sqls.add("delete from FSCHANGESINSTRUCTION where cs_cuid = "+cuid);
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(changesInstructionInfos, "FSCHANGESINSTRUCTION"));
				}
			}
			
			baseDao.execute(sqls);
			Object creditdate = baseDao.getFieldDataByCondition("CUSTCREDITRATINGAPPLY", "cra_auditdate", "cra_cuvename = '" + custname
					+ "' and cra_valid = 'VALID' and cra_type = '信用评级申请'");
			if (creditdate == null) {
				// 生成信用评级申请单
				Map<Object, Object> custCreditRatingApply = new HashMap<Object, Object>();
				int cra_id = baseDao.getSeqId("CUSTCREDITRATINGAPPLY_SEQ");
				custCreditRatingApply.put("cra_id", cra_id);
				custCreditRatingApply.put("cra_type", "信用评级申请");
				custCreditRatingApply.put("cra_code", baseDao.sGetMaxNumber("CUSTCREDITRATINGAPPLY", 2));
				custCreditRatingApply.put("cra_applyman", financingApply.get("fa_applyman"));
				custCreditRatingApply.put("cra_date", financingApply.get("fa_applydate"));
				custCreditRatingApply.put("cra_status", BaseUtil.getLocalMessage("ENTERING"));
				custCreditRatingApply.put("cra_issyscust", 0);
				custCreditRatingApply.put("cra_cuvecode", custInfo.get("cu_enuu"));
				custCreditRatingApply.put("cra_cuvename", custname);
				custCreditRatingApply.put("cra_statuscode", "ENTERING");
				custCreditRatingApply.put("cra_indate", DateUtil.format(new Date(), Constant.YMD));
				baseDao.execute(SqlUtil.getInsertSqlByMap(custCreditRatingApply, "CustCreditRatingApply"));
			}

			boolean bool = baseDao.checkByCondition("CUSTCREDITRATINGAPPLY", "cra_cuvename = '" + custname
					+ "' and cra_valid = 'VALID' and cra_type = '企业信用风险'");
			if (bool) {
				// 生成企业信用风险
				Map<Object, Object> custCreditRatingApply = new HashMap<Object, Object>();
				int cra_id = baseDao.getSeqId("CUSTCREDITRATINGAPPLY_SEQ");
				custCreditRatingApply.put("cra_id", cra_id);
				custCreditRatingApply.put("cra_type", "企业信用风险");
				custCreditRatingApply.put("cra_code", baseDao.sGetMaxNumber("CUSTCREDITRATINGAPPLY", 2));
				custCreditRatingApply.put("cra_applyman", financingApply.get("fa_applyman"));
				custCreditRatingApply.put("cra_date", financingApply.get("fa_applydate"));
				custCreditRatingApply.put("cra_status", BaseUtil.getLocalMessage("ENTERING"));
				custCreditRatingApply.put("cra_issyscust", 0);
				custCreditRatingApply.put("cra_cuvecode", custInfo.get("cu_enuu"));
				custCreditRatingApply.put("cra_cuvename", custname);
				custCreditRatingApply.put("cra_statuscode", "ENTERING");
				custCreditRatingApply.put("cra_indate", DateUtil.format(new Date(), Constant.YMD));
				baseDao.execute(SqlUtil.getInsertSqlByMap(custCreditRatingApply, "CustCreditRatingApply"));
			}
			if (null!=cu_code) {
				SqlRowList rs = baseDao.queryForRowSet(
						"select cu_webserver,cu_whichsystem,cu_secret FROM  CustomerInfor where cu_code = ? and nvl(cu_issys,0)<>0", cu_code);
				if (rs.next()) {
					String website = rs.getGeneralString("cu_webserver");
					String whichsystem = rs.getGeneralString("cu_whichsystem");
					String secret = rs.getGeneralString("cu_secret");
					if (StringUtil.hasText(website) && StringUtil.hasText(whichsystem) && StringUtil.hasText(secret)) {
						
						Map<String, String> params = new HashMap<String, String>();
						List<Integer> yearmonths = baseDao.getFieldValues("CustFAReport", "cr_yearmonth", "cr_cuname='" + custname + "' and cr_fatype <> '其他项目'", Integer.class);
					
						params.put("yearmonths", FlexJsonUtil.toJsonArray(yearmonths));
						boolean exitUDStream = baseDao.checkIf("CUSTOMERUDSTREAM LEFT JOIN CUSTOMERINFOR ON　CUD_CUID＝CU_ID", "cu_name = '"
								+ custname + "'");
						params.put("exitUDStream", String.valueOf(exitUDStream));
						params.put("right", String.valueOf(false));
						
						Response response = HttpUtil.sendPostRequest(website+"/openapi/factoring/getFaReports.action?master="+whichsystem, params, true, secret);
						if (response.getStatusCode() == HttpStatus.OK.value()) {
							String data = response.getResponseText();
							if (StringUtil.hasText(data)) {
								Map<Object, Object> datas = BaseUtil.parseFormStoreToMap(data);
								sqls = new ArrayList<String>();
								Object CustUDStream = datas.get("CustUDStream");
								List<Integer> yearmonths1 = FlexJsonUtil.fromJsonArray(String.valueOf(datas.get("yearmonths")), Integer.class);
								// 保存上下游关联方情况
								if (CustUDStream != null) {
									List<Map<Object, Object>> custUDStream = BaseUtil.parseGridStoreToMaps(CustUDStream.toString());
									for (int i = 0; i < custUDStream.size(); i++) {
										custUDStream.get(i).put("cud_id", baseDao.getSeqId("CUSTOMERUDSTREAM_SEQ"));
										custUDStream.get(i).put("cud_cuid", cuid);
										custUDStream.get(i).put("cud_detno", i + 1);
									}
									sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custUDStream, "CUSTOMERUDSTREAM"));
								}
		
								Object CustFaReportss = datas.get("CustFaReportss");
								// 复制财务报表
								List<String> custFaReportss = FlexJsonUtil.fromJsonArray(CustFaReportss.toString(), String.class);
								for (String CustFaReports : custFaReportss) {
									List<String> custFaReports = FlexJsonUtil.fromJsonArray(CustFaReports, String.class);
									for (int i = 0; i < custFaReports.size(); i++) {
										String str = custFaReports.get(i);
										List<Map<Object, Object>> custFaReportDetail = BaseUtil.parseGridStoreToMaps(str);
										if (custFaReportDetail.size() > 0) {
											Map<Object, Object> custFaReport = new HashMap<Object, Object>();
											int cr_id = baseDao.getSeqId("CUSTFAREPORT_SEQ");
											custFaReport.put("cr_id", cr_id);
											custFaReport.put("cr_date", custFaReportDetail.get(0).get("crd_indate"));
											custFaReport.put("cr_fatype", custFaReportDetail.get(0).get("crd_fsname"));
											custFaReport.put("cr_cuname", custname);
											custFaReport.put("cr_yearmonth", custFaReportDetail.get(0).get("crd_yearmonth"));
											custFaReport.put("cr_cucode", cu_code);
											sqls.add(SqlUtil.getInsertSqlByMap(custFaReport, "CustFAReport"));
		
											for (Map<Object, Object> map : custFaReportDetail) {
												int crd_id = baseDao.getSeqId("CUSTFAREPORTDETAIL_SEQ");
												map.put("crd_id", crd_id);
												map.put("crd_crid", cr_id);
											}
											sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custFaReportDetail, "CustFAReportDetail"));
										}
									}
								}
		
								// 复制其他报表
								Object CustOthReports = datas.get("CustOthReports");
								List<String> custOthReports = FlexJsonUtil.fromJsonArray(CustOthReports.toString(), String.class);
								for (int i = 0; i < custOthReports.size(); i++) {
									String str = custOthReports.get(i);
									List<Map<Object, Object>> custOthReportDetail = BaseUtil.parseGridStoreToMaps(str);
									if (custOthReportDetail.size() > 0) {
										Map<Object, Object> custFaReport = new HashMap<Object, Object>();
										Object type = custOthReportDetail.get(0).get("crd_fsname");
										custFaReport.put("cr_date", custOthReportDetail.get(0).get("crd_indate"));
										custFaReport.put("cr_fatype", custOthReportDetail.get(0).get("crd_fsname"));
										custFaReport.put("cr_cuname", custname);
										custFaReport.put("cr_yearmonth", custOthReportDetail.get(0).get("crd_yearmonth"));
										custFaReport.put("cr_cucode", cu_code);
		
										Object cr_id = baseDao.getFieldDataByCondition("CustFAReport", "cr_id", "cr_fatype = '" + type
												+ "' and cr_cucode = '" + cu_code + "'");
		
										if (StringUtil.hasText(cr_id)) {
											custFaReport.put("cr_id", cr_id);
											sqls.add(SqlUtil.getUpdateSqlByFormStore(custFaReport, "CustFAReport", "cr_id"));
											baseDao.deleteByCondition("CustFAReportDetail", "crd_crid = ?", cr_id);
										} else {
											cr_id = baseDao.getSeqId("CUSTFAREPORT_SEQ");
											custFaReport.put("cr_id", cr_id);
											sqls.add(SqlUtil.getInsertSqlByMap(custFaReport, "CustFAReport"));
										}
		
										for (Map<Object, Object> map : custOthReportDetail) {
											int crd_id = baseDao.getSeqId("CUSTFAREPORTDETAIL_SEQ");
											map.put("crd_id", crd_id);
											map.put("crd_crid", cr_id);
										}
										sqls.addAll(SqlUtil.getInsertSqlbyGridStore(custOthReportDetail, "CustFAReportDetail"));
									}
								}
								baseDao.execute(sqls);
								// 计算申请得分
								callProcedure(yearmonths1, String.valueOf(custname));
							}
						} else {
							throw new RuntimeException("连接客户账套失败," + response.getStatusCode());
						}
					}
				}
			}

			// 生成保理额度申请单
			sqls = new ArrayList<String>();
			Map<Object, Object> customerQuota = new HashMap<Object, Object>();
			Integer cqid = baseDao.getSeqId("CUSTOMERQUOTA_SEQ");
			cqcode = baseDao.sGetMaxNumber("CUSTOMERQUOTA", 2);

			customerQuota.put("cq_id", cqid);
			customerQuota.put("cq_code", cqcode);
			customerQuota.put("cq_custcode", cu_code);
			customerQuota.put("cq_custname", custname);
			customerQuota.put("cq_currency", custInfo.get("cu_capcurrency"));
			customerQuota.put("cq_applyquota", financingApply.get("fa_appamount"));
			customerQuota.put("cq_quotatype", "正向保理业务");
			customerQuota.put("cq_type", "公开型有追索权国内保理");
			customerQuota.put("cq_status", BaseUtil.getLocalMessage("ENTERING"));
			customerQuota.put("cq_statuscode", "ENTERING");
			customerQuota.put("cq_indate", DateUtil.format(new Date(), Constant.YMD));
			customerQuota.put("cq_finid", b2bid);

			sqls.add(SqlUtil.getInsertSqlByMap(customerQuota, "CUSTOMERQUOTA"));

			// 生成买方客户资料
			List<Map<Object, Object>> purcCustInfos = BaseUtil.parseGridStoreToMaps(mfCust);
			if (!CollectionUtils.isEmpty(purcCustInfos)) {
				int detno = 0;
				for (Map<Object, Object> purcCust : purcCustInfos) {
					purcCust.put("mf_sourcecode", purcCust.get("id"));
					purcCust.remove("enuu");
					Object[] Cust = baseDao.getFieldsDataByCondition("CustomerInfor","cu_id,cu_code","cu_name = '" + purcCust.get("mf_custname") + "'");
					Map<Object, Object> cust = new HashMap<Object, Object>();
					Object custcode = null;
					cust.put("cu_name", purcCust.get("mf_custname"));
					cust.put("cu_corporation",purcCust.get("mf_legrep"));
					cust.put("cu_regcapital",purcCust.get("mf_regcapital"));
					cust.put("cu_officeadd",purcCust.get("mf_addr"));
					cust.put("cu_licensedate",purcCust.get("mf_estabtime"));
					cust.put("cu_contact",purcCust.get("mf_contact"));
					cust.put("cu_contactnum",purcCust.get("mf_contactnum"));
					cust.put("cu_businsscope",purcCust.get("mf_businsscope"));
					if (null==Cust) {
						custcode = baseDao.sGetMaxNumber("CUSTOMERINFOR", 2);
						cust.put("cu_id", baseDao.getSeqId("CUSTOMERINFOR_SEQ"));
						cust.put("cu_code", custcode);
						cust.put("cu_date", DateUtil.getCurrentDate());
						cust.put("cu_status", BaseUtil.getLocalMessage("ENTERING"));
						cust.put("cu_statuscode", "ENTERING");
						sqls.add(SqlUtil.getInsertSqlByMap(cust, "CUSTOMERINFOR"));
					}else{
						cust.put("cu_id", Cust[0]);
						custcode = Cust[1];
						sqls.add(SqlUtil.getUpdateSqlByFormStore(cust, "CUSTOMERINFOR","cu_id"));
					}
					int mfid = baseDao.getSeqId("FSMFCUSTINFO_SEQ");
					purcCust.put("mf_id", mfid);
					purcCust.put("mf_cqid", cqid);
					purcCust.put("mf_detno", ++detno);
					purcCust.put("mf_custcode", custcode);
					purcCust.put("mf_indate", DateUtil.getCurrentDate());
					purcCust.put("mf_status", BaseUtil.getLocalMessage("ENTERING"));
					purcCust.put("mf_statuscode", "ENTERING");
					
					String mfCustDet = String.valueOf(purcCust.get("purcCustInfoDetails"));
					if (StringUtil.hasText(mfCustDet)&&!"null".equals(mfCustDet)) {
						List<Map<Object, Object>> purcCustDetInfos = BaseUtil.parseGridStoreToMaps(mfCustDet);
						if (!CollectionUtils.isEmpty(purcCustDetInfos)) {
							for (Map<Object, Object> purcCustDet : purcCustDetInfos) {
								purcCustDet.remove("enuu");
								purcCustDet.remove("mfId");
								int mfdid = baseDao.getSeqId("FSMFCUSTINFODET_SEQ");
								purcCustDet.put("mfd_id", mfdid);
								purcCustDet.put("mfd_cqid", cqid);
								purcCustDet.put("mfd_mfid", mfid);
							}
							sqls.addAll(SqlUtil.getInsertSqlbyGridStore(purcCustDetInfos, "FSMFCUSTINFODET"));
						}
					}
					purcCust.remove("purcCustInfoDetails");
				}
				sqls.addAll(SqlUtil.getInsertSqlbyGridStore(purcCustInfos, "FSMFCUSTINFO"));
			}
			
			// 经营情况描述
			Map<Object, Object> conditionInfo = BaseUtil.parseFormStoreToMap(businessCondition);
			if (null!=conditionInfo) {
				conditionInfo.remove("enuu");
				conditionInfo.put("bc_id", cqid);
				conditionInfo.put("bc_cuid", cuid);
				sqls.add(SqlUtil.getInsertSqlByMap(conditionInfo, "BUSINESSCONDITION"));
			}
			
			//上/今年经营情况
			List<Map<Object, Object>> mixInfos = BaseUtil.parseGridStoreToMaps(prouductMixe);
			if (!CollectionUtils.isEmpty(mixInfos)) {
				int detno = 0;
				for (Map<Object, Object> mixInfo : mixInfos) {
					mixInfo.remove("enuu");
					int pmid = baseDao.getSeqId("BC_PRODUCTMIX_SEQ");
					mixInfo.put("pm_id", pmid);
					mixInfo.put("pm_bcid", cqid);
					mixInfo.put("pm_detno", ++detno);
				}
				sqls.addAll(SqlUtil.getInsertSqlbyGridStore(mixInfos, "BC_PRODUCTMIX"));
			}
			
			// 前五大客户/供应商
			List<Map<Object, Object>> UpdowncastInfos = BaseUtil.parseGridStoreToMaps(upDownCast);
			if (!CollectionUtils.isEmpty(UpdowncastInfos)) {
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(UpdowncastInfos, new Object[] { "udc_kind"});
				for (Object m : map.keySet()) {
					int detno = 0;
					List<Map<Object, Object>> updowncastInfos = map.get(m);
					for (Map<Object, Object> updowncast : updowncastInfos) {
						updowncast.remove("enuu");
						int udcid = baseDao.getSeqId("BC_UPDOWNCUST_SEQ");
						updowncast.put("udc_id", udcid);
						updowncast.put("udc_bcid", cqid);
						updowncast.put("udc_detno", ++detno);
					}
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(updowncastInfos, "BC_UPDOWNCUST"));
				}
			}
			
			// 财务情况
			Map<Object, Object> financeCondition = BaseUtil.parseFormStoreToMap(FinanceCondition);
			if (null!=financeCondition) {
				financeCondition.remove("enuu");
				financeCondition.put("fc_caid", cqid);
				financeCondition.put("fc_cuid", cuid);
				sqls.add(SqlUtil.getInsertSqlByMap(financeCondition, "FINANCCONDITION"));
			}
			
			// 财务账款
			List<Map<Object, Object>> AccountInfos = BaseUtil.parseGridStoreToMaps(accountList);
			if (!CollectionUtils.isEmpty(AccountInfos)) {
				Map<Object, List<Map<Object, Object>>> map = BaseUtil.groupsMap(AccountInfos, new Object[] { "ai_kind"});
				for (Object m : map.keySet()) {
					int detno = 0;
					List<Map<Object, Object>> accountInfos = map.get(m);
					for (Map<Object, Object> account : accountInfos) {
						account.remove("enuu");
						int aiid = baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ");
						account.put("ai_id", aiid);
						account.put("ai_alid", cqid);
						account.put("ai_detno", ++detno);
					}
					sqls.addAll(SqlUtil.getInsertSqlbyGridStore(accountInfos, "AL_ACCOUNTINFOR"));
				}
			}
			
			baseDao.execute(sqls);
			
			List<String> clobFields = null;
			List<String> clobStrs = null;
			if (null!=conditionInfo) {
				clobFields = new ArrayList<String>();
				clobStrs = new ArrayList<String>();
				for (Object field : conditionInfo.keySet()) {
					Object value = conditionInfo.get(field);
					if (value != null) {
						String val = value.toString();
						if (val.length() > 2000) {
							clobFields.add(field.toString());
							clobStrs.add(val);
						}
					}
				}
				baseDao.saveClob("BUSINESSCONDITION", clobFields, clobStrs, "bc_id=" + cqid);
			}
			if (null!=financeCondition) {
				clobFields = new ArrayList<String>();
				clobStrs = new ArrayList<String>();
				for (Object field : financeCondition.keySet()) {
					Object value = financeCondition.get(field);
					if (value != null) {
						String val = value.toString();
						if (val.length() > 2000) {
							clobFields.add(field.toString());
							clobStrs.add(val);
						}
					}
				}
				baseDao.saveClob("FINANCCONDITION", clobFields, clobStrs, "fc_caid=" + cqid);
			}
			
			result.put("busincode", cqcode);
			
			// 存储改错
			baseDao.procedure("FS_FSFINANCEITEMS", new Object[] { cqid, "保理额度申请" });

			if (employees != null) {
				String context = "<font color=\"#0000ff\"><a href=\"javascript:openUrl(''jsps/fs/cust/customerQuota.jsp?formCondition=cq_idIS"
						+ cqid
						+ "&_noc=1&datalistId=NaN'')\" style=\"font-size:14px; color:blue;\">"
						+ custname
						+ "</a></font>有新增申请";
				beatchNotices(context, "system", "金融知会消息", employees, "CustomerQuota", cqid, cqcode);
			}

			// 生成申请进度
			Map<Object, Object> finapply = new HashMap<Object, Object>();
			int fsid = baseDao.getSeqId("FINBUSINAPPLY_SEQ");
			String now = DateUtil.format(new Date(), Constant.YMD_HMS);
			finapply.put("fs_id", fsid);
			finapply.put("fs_cqcode", cqcode);
			finapply.put("fs_custcode", cu_code);
			finapply.put("fs_custname", custname);
			finapply.put("fs_applydate", now);
			finapply.put("fs_crdratdate", StringUtil.hasText(creditdate) ? now : "");
			if (StringUtil.hasText(creditdate)) {
				finapply.put("fs_status", "应收账款转让");
			} else {
				finapply.put("fs_status", "收集材料");
			}

			baseDao.execute(SqlUtil.getInsertSqlByMap(finapply, "FINBUSINAPPLY"));
			if (null!=parentMaster) {
				SpObserver.putSp(sob);
				baseDao.execute(SqlUtil.getInsertSqlByMap(finapply, "FINBUSINAPPLY"));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e.getMessage());
		}
		return result;
	}
	
	@Override
	public void AssignRecBalance(String cqcode, String sales, String custcode,String custname) {
		try {
			cqcode = URLDecoder.decode(cqcode, "utf-8");
			sales = URLDecoder.decode(sales, "utf-8");
			custcode = URLDecoder.decode(custcode, "utf-8");
			custname = URLDecoder.decode(custname, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		String sob = SpObserver.getSp();
		Master master = enterpriseService.getMasterByName(sob);
		Master parentMaster = null;
		if (master != null && master.getMa_pid()!= null && master.getMa_pid() > 0) {
			parentMaster = enterpriseService.getMasterByID(master.getMa_pid());
		}
		if (parentMaster!=null) {
			SpObserver.putSp(parentMaster.getMa_name());
		}else{
			SpObserver.putSp(sob);
		}
		
		Object[] customerQuota = baseDao.getFieldsDataByCondition("CustomerQuota LEFT JOIN FSMFCUSTINFO ON CQ_ID = MF_CQID", 
				new String[] { "cq_custcode", "cq_custname","MF_CUSTCODE", "MF_CUSTNAME", "cq_recorder" }, 
				"cq_code = '" + cqcode + "' and ((NVL(MF_SOURCECODE,' ')<>' ' AND MF_SOURCECODE = '"+custcode+"') "
				+ "OR (NVL(MF_SOURCECODE,' ')=' ' AND MF_CUSTNAME = '"+custname+"'))");
		
		List<Map<Object, Object>> list = BaseUtil.parseGridStoreToMaps(sales);
		for (Map<Object, Object> map : list) {
			map.put("id", baseDao.getSeqId("FSSALE_SEQ"));
			map.put("code", baseDao.sGetMaxNumber("FSSALE", 2));
			map.put("sa_custcode", customerQuota[0]);
			map.put("sa_custname", customerQuota[1]);
			map.put("sa_mfcustcode", customerQuota[2]);
			map.put("sa_mfcustname", customerQuota[3]);
			map.put("recorder", customerQuota[4]);
			map.put("indate", DateUtil.getCurrentDate());
			map.put("status", BaseUtil.getLocalMessage("ENTERING"));
			map.put("statuscode", "ENTERING");
		}
		baseDao.execute(SqlUtil.getInsertSqlbyGridStore(list, "FSSALE"));
	}


	@Override
	public List<Map<Object, Object>> FinancApplyProgress(String busincode) {
		List<Map<Object, Object>> result = new ArrayList<Map<Object, Object>>();
		SqlRowList rs = baseDao
				.queryForRowSet(
						"select fs_status,fs_applydate,fs_crdratdate,fs_acceptdate,fs_statusdate,fs_loaddate from FINBUSINAPPLY where fs_cqcode = ?",
						busincode);
		if (rs.next()) {
			Map<Object, Object> map = new HashMap<Object, Object>();
			map.put("desc", "平台申请");
			if (rs.getDate("fs_applydate") != null) {
				map.put("date", DateUtil.format(rs.getDate("fs_applydate"), "yyyy年MM月dd日"));
				map.put("isok", "greencircle");
			} else {
				map.put("date", "");
				map.put("isok", "redcircle");
			}
			result.add(map);
			if (rs.getString("fs_status") != null && "申请取消".equals(rs.getString("fs_status"))) {
				map = new HashMap<Object, Object>();
				map.put("desc", "申请取消");
				if (rs.getDate("fs_crdratdate") != null) {
					map.put("date", DateUtil.format(rs.getDate("fs_acceptdate"), "yyyy年MM月dd日"));
					map.put("isok", "greencircle");
				} else {
					map.put("date", "");
					map.put("isok", "redcircle");
				}
				result.add(map);
			} else {
				map = new HashMap<Object, Object>();
				map.put("desc", "收集材料");
				if (rs.getDate("fs_crdratdate") != null) {
					map.put("date", DateUtil.format(rs.getDate("fs_crdratdate"), "yyyy年MM月dd日"));
					map.put("isok", "greencircle");
				} else {
					map.put("date", "");
					map.put("isok", "redcircle");
				}
				result.add(map);
				map = new HashMap<Object, Object>();
				map.put("desc", "应收账款转让");
				if (rs.getDate("fs_acceptdate") != null) {
					map.put("date", DateUtil.format(rs.getDate("fs_acceptdate"), "yyyy年MM月dd日"));
					map.put("isok", "greencircle");
				} else {
					map.put("date", "");
					map.put("isok", "redcircle");
				}
				result.add(map);
				map = new HashMap<Object, Object>();
				map.put("desc", "出账审批");
				if (rs.getDate("fs_statusdate") != null) {
					map.put("date", DateUtil.format(rs.getDate("fs_statusdate"), "yyyy年MM月dd日"));
					map.put("isok", "greencircle");
				} else {
					map.put("date", "");
					map.put("isok", "redcircle");
				}
				result.add(map);
				map = new HashMap<Object, Object>();
				map.put("desc", "放款");
				if (rs.getDate("fs_loaddate") != null) {
					map.put("date", DateUtil.format(rs.getDate("fs_loaddate"), "yyyy年MM月dd日"));
					map.put("isok", "greencircle");
				} else {
					map.put("date", "");
					map.put("isok", "redcircle");
				}
				result.add(map);
			}
		}
		return result;
	}

	@Override
	public void AccountApplyFromB2B(String apply, Long faid, String mfcustname, String fsSales, String receipts, String attaches) {
		
		if (!isEmpty(apply)) {
			Map<Object, Object> accountApply = BaseUtil.parseFormStoreToMap(apply);
			Object b2bid = accountApply.get("id");
			if (baseDao.checkIf("ACCOUNTAPPLY", "aa_aaid = "+b2bid)) {
				return;
			}
			List<String> sqls = new ArrayList<String>();
			accountApply.put("aa_aaid", b2bid);
			accountApply.remove("cqId");
			Object indate = isEmpty(accountApply.get("indate"))?DateUtil.getCurrentDate():accountApply.get("indate");
			accountApply.remove("indate");
			accountApply.remove("erpstatus");
			accountApply.remove("fauu");
			int aaid = baseDao.getSeqId("ACCOUNTAPPLY_SEQ");
			accountApply.put("aa_id", aaid);
			accountApply.put("aa_code", baseDao.sGetMaxNumber("ACCOUNTAPPLY", 2));
			Object custcode = null,custname = null,mfcustcode = null;
			
			Object[] custQuota = baseDao.getFieldsDataByCondition("CustomerQuota", new String[]{"cq_id","cq_code","cq_custcode","cq_custname","cq_currency","cq_quota","cq_lendrate","cq_handrate","cq_assuremeans","cq_truster"}, "cq_statuscode='AUDITED' and cq_finid = "+faid);
			if (custQuota==null) {
				return;
			}
			
			custcode = custQuota[2];
			custname = custQuota[3];
			accountApply.put("aa_cacode", custQuota[1]);
			accountApply.put("aa_custcode",custcode);
			accountApply.put("aa_custname", custname);
			accountApply.put("aa_currency", custQuota[4]);
			accountApply.put("aa_factoring", custQuota[5]);
			accountApply.put("aa_lendrate", custQuota[6]);
			accountApply.put("aa_handrate", custQuota[7]);
			accountApply.put("aa_assuremeans", custQuota[8]);
			accountApply.put("aa_truster", custQuota[9]);
			mfcustcode = baseDao.getFieldDataByCondition("FSMFCUSTINFO", "mf_custcode", "mf_custname = '"+mfcustname+"' and mf_cqid = "+custQuota[0]);
			
			accountApply.put("aa_mfcustcode", mfcustcode);
			accountApply.put("aa_mfcustname", mfcustname);
			accountApply.put("aa_status", BaseUtil.getLocalMessage("ENTERING"));
			accountApply.put("aa_statuscode", "ENTERING");
			accountApply.put("aa_indate", indate);
			accountApply.remove("id");
			sqls.add(SqlUtil.getInsertSqlByMap(accountApply, "ACCOUNTAPPLY"));
			
			//附件管理
			List<Map<Object, Object>> Attaches = BaseUtil.parseGridStoreToMaps(attaches);
			if (!CollectionUtils.isEmpty(Attaches)) {
				sqls.addAll(saveAttaches(Attaches, aaid, "AccountApply", null));
			}
			List<Map<Object, Object>> FsSales = BaseUtil.parseGridStoreToMaps(fsSales);	
			if (!CollectionUtils.isEmpty(FsSales)) {
					
				List<Map<Object, Object>> accountApplyDets = new ArrayList<Map<Object,Object>>();
				int detno = 0;
				for (Map<Object, Object> fsSale : FsSales) {
					fsSale.remove("aa_id");
					fsSale.put("id", baseDao.getSeqId("FSSALE_SEQ"));
					String code = baseDao.sGetMaxNumber("FSSALE", 2);
					fsSale.put("code", code);
					fsSale.put("sa_custcode",custcode);
					fsSale.put("sa_custname", custname);
					fsSale.put("sa_mfcustcode", mfcustcode);
					fsSale.put("sa_mfcustname", mfcustname);
					fsSale.put("sa_currency", "RMB");
					fsSale.put("sa_rate", 1);
					fsSale.put("status", BaseUtil.getLocalMessage("ENTERING"));
					fsSale.put("statuscode", "ENTERING");
					fsSale.put("indate", indate);
					Map<Object, Object> accountApplyDet = new HashMap<Object, Object>();
					accountApplyDet.put("aas_id", baseDao.getSeqId("ACCOUNTAPPLYSA_SEQ"));
					accountApplyDet.put("aas_aaid", aaid);
					accountApplyDet.put("aas_detno", ++detno);
					accountApplyDet.put("aas_sacode", code);
					accountApplyDet.put("aas_contractno", fsSale.get("sa_contractno"));
					accountApplyDet.put("aas_amount", fsSale.get("sa_contractamount"));
					accountApplyDets.add(accountApplyDet);
				}
				sqls.addAll(SqlUtil.getInsertSqlbyGridStore(FsSales, "FsSale"));
				sqls.addAll(SqlUtil.getInsertSqlbyGridStore(accountApplyDets, "ACCOUNTAPPLYSA"));
			}
			
			List<Map<Object, Object>> Receipts = BaseUtil.parseGridStoreToMaps(receipts);
			if (!CollectionUtils.isEmpty(Receipts)) {
				int detno = 0;
				for (Map<Object, Object> receipt : Receipts) {
					receipt.remove("id");
					receipt.remove("aa_id");
					receipt.put("aai_id", baseDao.getSeqId("ACCOUNTAPPLYINV_SEQ"));
					receipt.put("aai_aaid", aaid);
					receipt.put("aai_detno", ++detno);
				}
				
				sqls.addAll(SqlUtil.getInsertSqlbyGridStore(Receipts, "ACCOUNTAPPLYINV"));
			}
			baseDao.execute(sqls);
			baseDao.execute("update ACCOUNTAPPLY set aa_billamount=nvl((select sum(aai_amount) from ACCOUNTAPPLYINV where aai_aaid=aa_id),0) where aa_id="
					+ aaid);
			baseDao.execute("update ACCOUNTAPPLY set aa_saamount=nvl((select sum(aas_amount) from ACCOUNTAPPLYSA where aas_aaid=aa_id),0) where aa_id="
					+ aaid);
			baseDao.execute("update ACCOUNTAPPLY set aa_lendrate=round(nvl(aa_wantamount,0)/nvl(aa_saamount,0)*100,2) where aa_id="
					+ aaid + " and nvl(aa_transferamount,0)<>0");
			baseDao.execute("update ACCOUNTAPPLY set aa_hand=round(nvl(aa_transferamount,0)*nvl(aa_handrate,0)/100,2) where aa_id=" + aaid);
		}
	}
	
}

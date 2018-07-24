package com.uas.erp.service.fs.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.DateUtil;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.core.SqlUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.bind.Constant;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.fs.ApiForFSService;

@Service
public class ApiForFSServiceImpl implements ApiForFSService {
	
	@Autowired
	private BaseDao baseDao;
	
	@Autowired
	private EmployeeDao employeeDao;
	
	@Override
	public Map<String, Object> getFaReports(String yearmonths, Boolean exitUDStream, Boolean right) {
		Map<String, Object> result = new HashMap<String, Object>();
		List<Integer> YearMonths = new ArrayList<Integer>();
		try {
			yearmonths = URLDecoder.decode(yearmonths, "utf-8");
			
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}
		if (right) {
			YearMonths = FlexJsonUtil.fromJsonArray(yearmonths, Integer.class);
		}else{
			List<Integer> yearMonths = FlexJsonUtil.fromJsonArray(yearmonths, Integer.class);
			Integer year = null;
			try {
				year = DateUtil.getYear(new Date());
			} catch (ParseException e1) {
				e1.printStackTrace();
			}

			// 总账结账期间上一期间
			Integer GLYearmonth = baseDao
					.queryForObject(
							"Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code = 'MONTH-A' And Pd_Status =99 Order By rpad(Pd_Detno,7,0) Desc) Where Rownum=1",
							Integer.class);
			if (GLYearmonth!=null&&!yearMonths.contains(GLYearmonth)) {
				YearMonths.add(GLYearmonth);
			}
			

			for (int i = 1; i < 4; i++) {
				Integer yearmonth = baseDao.queryForObject(
						"select max(substr(frd_yearmonth,1,6)) from FAREPORTDETAIL where substr(frd_yearmonth,1,4) = ?", Integer.class, year - i);
				if (yearmonth != null&&!yearMonths.contains(yearmonth)) {
					YearMonths.add(yearmonth);
				}
			}
		}

		// 获取前5条上下游关联方
		if (!exitUDStream) {
			List<Map<String, Object>> CustUDStream = new ArrayList<Map<String, Object>>();

			SqlRowList rs = baseDao
					.queryForRowSet("SELECT VE_NSRZH,PU_VENDCODE,PU_VENDNAME,VE_KIND,VE_LEGALMAN,VE_INITDATE,VE_BUSINESSRANGE,"
							+ "VE_CURRENCY,TOTAL1,TOTAL FROM (SELECT VE_NSRZH,PU_VENDCODE,PU_VENDNAME,VE_KIND,VE_LEGALMAN,VE_INITDATE,VE_BUSINESSRANGE,"
							+ "VE_CURRENCY,TOTAL1,TOTAL FROM (SELECT PU_VENDCODE,PU_VENDNAME,ROUND(SUM(NVL(PU_TOTAL,0)*NVL(PU_RATE,1)),2) TOTAL,"
							+ "ROUND(SUM(NVL(PU_TOTAL,0)),2) TOTAL1 FROM PURCHASE WHERE PU_STATUSCODE = 'AUDITED' GROUP BY  PU_VENDCODE,PU_VENDNAME) "
							+ "LEFT JOIN VENDOR ON PU_VENDCODE = VE_CODE ORDER BY TOTAL DESC) WHERE ROWNUM <=5");
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cud_paperstype", "纳税人识别号");
				map.put("cud_paperscode", rs.getString("VE_NSRZH"));
				map.put("cud_name", rs.getString("PU_VENDNAME"));
				map.put("cud_relation", "上游");
				map.put("cud_association", rs.getString("VE_KIND"));
				map.put("cud_legalperson", rs.getString("VE_LEGALMAN"));
				map.put("cud_buassdate", rs.getString("VE_INITDATE"));
				map.put("cud_product", rs.getString("VE_BUSINESSRANGE"));
				map.put("cud_currency", rs.getString("VE_CURRENCY"));
				map.put("cud_amount", rs.getString("TOTAL1"));
				CustUDStream.add(map);
			}

			rs = baseDao
					.queryForRowSet("SELECT CU_TAXID,SA_CUSTCODE,SA_CUSTNAME,CU_KIND,CU_LAWMAN,CU_FOUNDTIME,CU_BUSINESSRANGE,CU_CURRENCY,"
							+ "TOTAL1,CU_INITDATE,TOTAL FROM (SELECT CU_TAXID,SA_CUSTCODE,SA_CUSTNAME,CU_KIND,CU_LAWMAN,CU_FOUNDTIME,CU_BUSINESSRANGE,"
							+ "CU_CURRENCY,TOTAL1,CU_INITDATE,TOTAL FROM (SELECT SA_CUSTCODE,SA_CUSTNAME,ROUND(SUM(NVL(SA_TOTAL,0)*NVL(SA_RATE,1)),2) "
							+ "TOTAL,ROUND(SUM(NVL(SA_TOTAL,0)),2) TOTAL1 FROM SALE WHERE SA_STATUSCODE = 'AUDITED' GROUP BY  SA_CUSTCODE,SA_CUSTNAME) "
							+ "LEFT JOIN CUSTOMER ON SA_CUSTCODE = CU_CODE  ORDER BY TOTAL DESC) WHERE ROWNUM <=5");
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("cud_paperstype", "纳税人识别号");
				map.put("cud_paperscode", rs.getString("CU_TAXID"));
				map.put("cud_name", rs.getString("SA_CUSTNAME"));
				map.put("cud_relation", "下游");
				map.put("cud_association", rs.getString("CU_KIND"));
				map.put("cud_legalperson", rs.getString("CU_LAWMAN"));
				map.put("cud_buassdate", rs.getString("CU_FOUNDTIME"));
				map.put("cud_product", rs.getString("CU_BUSINESSRANGE"));
				map.put("cud_currency", rs.getString("CU_CURRENCY"));
				map.put("cud_amount", rs.getString("TOTAL1"));
				map.put("cud_regdate", rs.getString("CU_INITDATE"));
				CustUDStream.add(map);
			}
			result.put("CustUDStream", FlexJsonUtil.toJsonArray(CustUDStream));
		}

	

		List<String> custfareportss = new ArrayList<String>();
		Object enname = baseDao.getFieldDataByCondition("Enterprise", "en_name", "1=1");
		for (Integer yearmonth : YearMonths) {
			List<String> custfareports = new ArrayList<String>();

			List<Object> fs_codes = baseDao.getFieldDatasByCondition("FAReport", "fr_fscode",
					"fr_fscode in ('A01','P01','C01') and fr_yearmonth = " + yearmonth);
			for (Object fscode : fs_codes) {
				baseDao.execute("update FAREPORTDETAIL set frd_standardfsname='现金流量表',frd_standardfscode='C01' where frd_fsname like '现金流量表%' and Frd_Fscode='"
						+ fscode + "' and frd_yearmonth=" + yearmonth + " and nvl(frd_standardfsname,' ')=' '");
				baseDao.execute("update FAREPORTDETAIL set frd_standardfsname='损益表',frd_standardfscode='P01' where frd_fsname like '利润表%' and Frd_Fscode='"
						+ fscode + "' and frd_yearmonth=" + yearmonth + " and nvl(frd_standardfsname,' ')=' '");
				baseDao.execute("update FAREPORTDETAIL set frd_standardfsname='损益表',frd_standardfscode='P01' where frd_fsname like '损益表%' and Frd_Fscode='"
						+ fscode + "' and frd_yearmonth=" + yearmonth + " and nvl(frd_standardfsname,' ')=' '");
				baseDao.execute("update FAREPORTDETAIL set frd_standardfsname='资产负债表',frd_standardfscode='A01' where frd_fsname like '资产负债表%' and Frd_Fscode='"
						+ fscode + "' and frd_yearmonth=" + yearmonth + " and nvl(frd_standardfsname,' ')=' '");
				SqlRowList rs = baseDao.queryForRowSet("select * from FAREPORTDETAIL where frd_yearmonth = ? and FRD_FSCODE = ?",
						yearmonth, fscode);
				List<Map<String, Object>> custfareport = new ArrayList<Map<String, Object>>();
				while (rs.next()) {
					Map<String, Object> custfareportd = new HashMap<String, Object>();
					custfareportd.put("crd_detno", rs.getGeneralInt("frd_detno"));
					custfareportd.put("crd_cuname", enname);
					custfareportd.put("crd_yearmonth", rs.getGeneralInt("frd_yearmonth"));
					custfareportd.put("crd_name", rs.getString("frd_name"));
					custfareportd.put("crd_step", rs.getGeneralInt("frd_step"));
					custfareportd.put("crd_amount1", rs.getDouble("frd_amount1") * rs.getDouble("frd_rate"));
					custfareportd.put("crd_amount2", rs.getDouble("frd_amount2") * rs.getDouble("frd_rate"));
					custfareportd.put("crd_rate", rs.getDouble("frd_rate"));
					custfareportd.put("crd_rightname", rs.getString("frd_rightname"));
					custfareportd.put("crd_rightstep", rs.getGeneralInt("frd_rightstep"));
					custfareportd.put("crd_rightamount1", rs.getDouble("frd_rightamount1") * rs.getDouble("frd_rightrate"));
					custfareportd.put("crd_rightamount2", rs.getDouble("frd_rightamount2") * rs.getDouble("frd_rightrate"));
					custfareportd.put("crd_rightrate", rs.getDouble("frd_rightrate"));
					custfareportd.put("crd_fsname", rs.getString("frd_standardfsname"));
					custfareportd.put("crd_fscode", rs.getString("frd_standardfscode"));
					custfareportd.put("crd_indate", DateUtil.format(new Date(), Constant.YMD));
					custfareportd.put("crd_standard", rs.getString("frd_standard"));
					custfareportd.put("crd_rightstandard", rs.getString("crd_rightstandard"));
					custfareport.add(custfareportd);
				}
				custfareports.add(FlexJsonUtil.toJsonArray(custfareport));
			}
			custfareportss.add(FlexJsonUtil.toJsonArray(custfareports));
		}
		result.put("CustFaReportss", FlexJsonUtil.toJsonArray(custfareportss));

		// 其他类型报表
		List<String> custOthReports = new ArrayList<String>();
		// 总账结账期间上一期间
		Integer GLYearmonth = baseDao.queryForObject("Select Pd_Detno From (Select Pd_Detno From Periodsdetail Where Pd_Code = 'MONTH-A' "
				+ "And Pd_Status =99 Order By rpad(Pd_Detno,7,0) Desc) Where Rownum=1", Integer.class);

		List<String> oths = baseDao.queryForList("select or_typecode from OTHREPORT_TEM group by or_typecode order by or_typecode",
				String.class);
		for (String othcode : oths) {
			String res = baseDao.callProcedure("SP_COUNTOTHERREPORT", new Object[] { othcode });
			if (StringUtil.hasText(res) && !res.equals("OK")) {
				BaseUtil.showError(res);
			}
			SqlRowList rs = baseDao.queryForRowSet(
					"select * from OTHREPORT_TEM where OR_ISSTAND<> 0 and OR_TYPECODE = ? order by OR_DETNO", othcode);
			List<Map<String, Object>> custOthReport = new ArrayList<Map<String, Object>>();
			while (rs.next()) {
				Map<String, Object> custOthReportd = new HashMap<String, Object>();
				custOthReportd.put("crd_rate", 1);
				custOthReportd.put("crd_detno", rs.getGeneralInt("or_detno"));
				custOthReportd.put("crd_yearmonth", GLYearmonth);
				custOthReportd.put("crd_cuname", enname);
				custOthReportd.put("crd_name", rs.getString("or_description"));
				custOthReportd.put("crd_step", rs.getGeneralInt("or_detno"));
				custOthReportd.put("crd_amount1", rs.getDouble("or_value"));
				custOthReportd.put("crd_fsname", rs.getString("or_typename"));
				custOthReportd.put("crd_fscode", rs.getString("or_typecode"));
				custOthReportd.put("crd_indate", DateUtil.format(new Date(), Constant.YMD));
				custOthReportd.put("crd_standard", rs.getString("or_typename"));
				custOthReport.add(custOthReportd);
			}
			custOthReports.add(FlexJsonUtil.toJsonArray(custOthReport));
			baseDao.updateByCondition("OTHREPORT_TEM", "OR_VALUE = NULL", "OR_TYPECODE = '" + othcode + "'");
		}
		result.put("yearmonths", FlexJsonUtil.toJsonArray(YearMonths));
		result.put("CustOthReports", FlexJsonUtil.toJsonArray(custOthReports));
		return result;
	}
	
	@Override
	public Map<String, Object> getDefaultDataS(Integer lastym, String applydate, Boolean financcondition, Boolean bankflow,
			Boolean productmix, Boolean updowncust, Boolean monetaryfund, Boolean accountinforar, Boolean accountinforothar,
			Boolean accountinforpp, Boolean accountinforinv, Boolean accountinforfix, Boolean accountinforlb, Boolean accountinforap,
			Boolean accountinforothap, Boolean accountinforlong) {
		Map<String, Object> result = new HashMap<String, Object>();
		SqlRowList rs = null;
		String sql = null;
		// 获取收入及盈利情况-银行流水情况
		boolean useBank = baseDao.isDBSetting("useBank");
		String firstday = null;
		int yearmonth = 0;
		int year = 0;
		try {
			year = DateUtil.getYear(applydate);
			firstday = DateUtil.getLastYearDay(applydate);
			yearmonth = DateUtil.getYearmonth(applydate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (bankflow != null && bankflow) {
			List<Map<String, Object>> bankFlow = new ArrayList<Map<String, Object>>();
			if (useBank) {
				sql = "select ar_accountname accountname,sum(nvl(ar_deposit,0)*ar_accountrate) inamount,sum(nvl(ar_payment,0)*ar_accountrate) outamount from AccountRegister where to_char(ar_date,'yyyy-MM-dd') >= '"
						+ firstday
						+ "' and to_char(ar_date,'yyyy-MM-dd') <= '"
						+ applydate
						+ "' and ar_statuscode = 'POSTED' group by ar_accountcode, ar_accountname order by ar_accountcode";
			} else {
				sql = "select ca_description accountname,sum(nvl(vd_debit,0)) inamount, sum(nvl(vd_credit,0)) outamount from VoucherDetail inner join Voucher on vd_void = vo_id left join Category on vd_catecode=ca_code where to_char(vo_date,'yyyy-MM-dd') >= '"
						+ firstday
						+ "' and to_char(vo_date,'yyyy-MM-dd') <= '"
						+ applydate
						+ "'  and nvl(ca_iscashbank,0)<>0 and vo_statuscode = 'ACCOUNT' group by vd_catecode, ca_description order by vd_catecode";
			}
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("bf_detno", rs.getCurrentIndex() + 1);
				map.put("bf_account", rs.getString("accountname"));
				map.put("bf_startdate", firstday);
				map.put("bf_enddate", applydate);
				map.put("bf_inamount", rs.getDouble("inamount"));
				map.put("bf_outamount", rs.getDouble("outamount"));
				bankFlow.add(map);
			}
			result.put("bankflow", FlexJsonUtil.toJsonArray(bankFlow));
		}
		if (productmix != null && productmix) {
			Double amount = baseDao.getFieldValue("prodinout left join prodiodetail on pi_id=pd_piid",
					"sum(round(pd_sendprice*(pd_outqty-pd_inqty)*pi_rate/(1+pd_taxrate/100),2))",
					"pi_statuscode='POSTED' AND pi_class IN ('出货单','销售退货单') and to_char(pi_date,'yyyy')=" + (year - 1), Double.class);
			Double tamount = baseDao.getFieldValue("prodinout left join prodiodetail on pi_id=pd_piid",
					"sum(round(pd_sendprice*(pd_outqty-pd_inqty)*pi_rate/(1+pd_taxrate/100),2))",
					"pi_statuscode='POSTED' AND pi_class IN ('出货单','销售退货单') and to_char(pi_date,'yyyy')=" + year, Double.class);
			// 经营情况-主营产品/服务结构
			List<Map<String, Object>> productMix = new ArrayList<Map<String, Object>>();
			sql = "select pr_kind,case when saleamount=0 then 0 else round((saleamount-costamount)/saleamount*100,2) end rate,saleamount from ("
					+ "select nvl(pr_kind,'其他项') pr_kind,sum(round(pd_sendprice*(pd_outqty-pd_inqty)*pi_rate/(1+pd_taxrate/100),2)) saleamount"
					+ ",sum(round(pd_price*(pd_outqty-pd_inqty),2)) costamount from prodinout,prodiodetail,product "
					+ "where pi_id=pd_piid and pd_prodcode=pr_code and pi_statuscode='POSTED' AND PD_PICLASS IN ('出货单','销售退货单') and to_char(pi_date,'yyyy')="
					+ (year - 1) + " group by pr_kind)";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				SqlRowList rs1 = baseDao
						.queryForRowSet("select case when saleamount=0 then 0 else round((saleamount-costamount)/saleamount*100,2) end rate,saleamount from ("
								+ "select sum(round(pd_sendprice*(pd_outqty-pd_inqty)*pi_rate/(1+pd_taxrate/100),2)) saleamount"
								+ ",sum(round(pd_price*(pd_outqty-pd_inqty),2)) costamount from prodinout,prodiodetail,product "
								+ "where pi_id=pd_piid and pd_prodcode=pr_code and pi_statuscode='POSTED' AND PD_PICLASS IN ('出货单','销售退货单') and to_char(pi_date,'yyyy')="
								+ year + " and nvl(pr_kind,'其他项')='" + rs.getString("pr_kind") + "')");
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("pm_detno", rs.getCurrentIndex() + 1);
				map.put("pm_kind", rs.getString("pr_kind"));
				map.put("pm_grossprofit", rs.getDouble("rate"));
				map.put("pm_annualrevenue", rs.getDouble("saleamount"));
				map.put("pm_ratio", amount == 0 ? 0 : NumberUtil.formatDouble(rs.getDouble("saleamount") / amount * 100, 2));
				if (rs1.next()) {
					map.put("pm_tgrossprofit", rs1.getDouble("rate"));
					map.put("pm_tannualrevenue", rs1.getDouble("saleamount"));
					map.put("pm_tratio", tamount == 0 ? 0 : NumberUtil.formatDouble(rs1.getDouble("saleamount") / tamount * 100, 2));
				}
				productMix.add(map);
			}
			result.put("productmix", FlexJsonUtil.toJsonArray(productMix));
		}
		if (monetaryfund != null && monetaryfund) {
			// 资产情况-货币资金
			List<Map<String, Object>> monetaryFund = new ArrayList<Map<String, Object>>();
			sql = "Select ca_description,amount,Case When Sumamout=0 Then 0 Else Round(Amount/Sumamout*100,2) End rate "
					+ "from (SELECT cm_catecode,ca_description,NVL(CM_ENDDEBIT,0)-NVL(CM_ENDCREDIT,0) AMOUNT,"
					+ "(SELECT SUM(NVL(CM_ENDDEBIT,0)-NVL(CM_ENDCREDIT,0)) FROM CATEMONTH LEFT JOIN CATEGORY ON CM_CATECODE=CA_CODE "
					+ "Where Nvl(Ca_Cashflow,0)<>0 And Cm_Yearmonth=To_Char(Sysdate,'yyyymm') And Nvl(Ca_Subof,0)=0) Sumamout "
					+ "From Catemonth Left Join Category On Cm_Catecode=Ca_Code Where Nvl(Ca_Cashflow,0)<>0 And Cm_Yearmonth=" + yearmonth
					+ " And Nvl(Ca_Subof,0)=0) Order By Cm_Catecode";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "货币资金");
				map.put("ai_caname", rs.getString("ca_description"));
				map.put("ai_amount", rs.getString("amount"));
				map.put("ai_billamount", rs.getDouble("amount"));
				map.put("ai_rate", rs.getDouble("rate"));
				monetaryFund.add(map);
			}
			result.put("monetaryfund", FlexJsonUtil.toJsonArray(monetaryFund));
		}
		if (financcondition != null && financcondition) {
			Map<Object, Object> financCondition = new HashMap<Object, Object>();
			// 帐龄≤3个月以内的
			Double amount1 = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED' and to_date('" + applydate + "','yyyy-mm-dd')-trunc(ab_date)<=90", Double.class);
			// 3个月<帐龄≤6个月以内的
			Double amount2 = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED' and to_date('" + applydate + "','yyyy-mm-dd')-trunc(ab_date)>90 and to_date('"
									+ applydate + "','yyyy-mm-dd')-trunc(ab_date)<=180", Double.class);
			// 6个月<帐龄<=1年以内的
			Double amount4 = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED' and to_date('" + applydate + "','yyyy-mm-dd')-trunc(ab_date)>180 and to_date('"
									+ applydate + "','yyyy-mm-dd')-trunc(ab_date)<=365", Double.class);
			// 帐龄>1年以上的
			Double amount3 = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED' and to_date('" + applydate + "','yyyy-mm-dd')-trunc(ab_date)>365", Double.class);
			// 应收账款余额
			Double amount = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED'", Double.class);
			financCondition.put("fc_agingamount1", amount1);
			financCondition.put("fc_agingamount2", amount2);
			financCondition.put("fc_agingamount3", amount3);
			financCondition.put("fc_agingamount4", amount4);
			financCondition.put("fc_yeatmonth", lastym);
			financCondition.put("fc_arbalance", amount);
			result.put("financcondition", FlexJsonUtil.toJsonDeep(financCondition));
		}
		if (accountinforar != null && accountinforar) {
			// 资产情况-应收账款
			Double amount = baseDao
					.getFieldValue(
							"ARBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) ",
							"AB_STATUSCODE='POSTED'", Double.class);
			List<Map<String, Object>> accountInforAR = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> accountInforARDet = new ArrayList<Map<String, Object>>();
			sql = "select * from (select cu_code,cu_shortname,cu_name,cu_payments,cu_bank,cu_bankaccount,cu_contact,cu_tel,cu_add1,amount,billamount from "
					+ "(select ab_custcode,AB_CUSTNAME,SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) AMOUNT,"
					+ "SUM(ROUND(AB_ARAMOUNT*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) billamount from ARBILL,CURRENCYSMONTH where CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm') "
					+ "and AB_STATUSCODE='POSTED' group by AB_CUSTCODE,AB_CUSTNAME) left join customer on ab_custcode=cu_code where AMOUNT<>0 order by AMOUNT desc) where rownum < 6";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				SqlRowList rs2 = baseDao
						.queryForRowSet("SELECT amount,days FROM (SELECT SUM(ROUND((ROUND(AB_ARAMOUNT,2)-ROUND(NVL(AB_PAYAMOUNT,0),2))*CASE WHEN NVL(CM_CRRATE,0)=0 THEN 1 ELSE CM_CRRATE END,2)) AMOUNT,ROUND(SUM(TRUNC(NVL(AB_ACTPAYDATE,TO_DATE('"
								+ applydate
								+ "','yyyy-mm-dd')))-TRUNC(AB_PAYDATE))/COUNT(AB_CODE),2) DAYS "
								+ "from arbill,CURRENCYSMONTH where CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm') and ab_statuscode='POSTED' and ab_custcode='"
								+ rs.getObject("cu_code")
								+ "' AND TRUNC(NVL(ab_actpaydate,to_date('"
								+ applydate
								+ "','yyyy-mm-dd')))>TRUNC(ab_paydate)) WHERE AMOUNT>0");
				int ai_id = baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ");
				map.put("ai_id", ai_id);
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "应收账款");
				map.put("ai_cushortname", rs.getString("cu_shortname"));
				map.put("ai_cuname", rs.getString("cu_name"));
				map.put("ai_add", rs.getString("cu_add1"));
				map.put("ai_contact", rs.getString("cu_contact"));
				map.put("ai_tel", rs.getString("cu_tel"));
				map.put("ai_bank", rs.getString("cu_bank"));
				map.put("ai_bankno", rs.getString("cu_bankaccount"));
				map.put("ai_billamount", amount); // 欠款总额
				map.put("ai_amount", rs.getDouble("billamount")); // 合同金额
				map.put("ai_leftamount", rs.getDouble("amount"));
				map.put("ai_rate", amount == 0 ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				map.put("ai_payment", rs.getString("cu_payments"));
				if (rs2.next()) {
					map.put("ai_isover", -1);
					map.put("ai_overdays", rs2.getGeneralDouble("days"));
					map.put("ai_overamount", rs2.getGeneralDouble("amount"));
				}
				accountInforAR.add(map);
				// 欠款明细
				String sql1 = "select years,amount,pamount,case when days>0 then '是' else '否' end overdue from "
						+ "(select sum(ab_aramount) amount, to_char(ab_date,'yyyy') years,sum(ab_aramount-ab_payamount) pamount,sum(ab_actpaydate-ab_paydate) days "
						+ "from arbill left join customer on ab_custcode=cu_code where cu_name='" + rs.getString("cu_name")
						+ "' and ab_statuscode='POSTED' group by to_char(ab_date,'yyyy') order by to_char(ab_date,'yyyy'))";
				SqlRowList rs1 = baseDao.queryForRowSet(sql1);
				while (rs1.next()) {
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("aid_id", baseDao.getSeqId("AL_ACCOUNTINFORDETAIL_SEQ"));
					map1.put("aid_aiid", ai_id);
					map1.put("aid_detno", rs1.getCurrentIndex() + 1);
					map1.put("aid_kind", "应收账款");
					map1.put("aid_year", rs1.getInt("years"));
					map1.put("aid_amount", rs1.getDouble("amount"));
					map1.put("aid_chargeamount", rs1.getDouble("pamount"));
					map1.put("aid_overdue", rs1.getString("overdue"));
					accountInforARDet.add(map1);
				}
			}
			result.put("accountinforar", FlexJsonUtil.toJsonArray(accountInforAR));
			result.put("accountinforardet", FlexJsonUtil.toJsonArray(accountInforARDet));
		}
		if (accountinforothar != null && accountinforothar) {
			// 资产情况-其他应收账款
			String othCatecode = baseDao.getDBSetting("MonthAccount", "othCatecode");
			String cond = " ";
			if (!StringUtils.isEmpty(othCatecode)) {
				cond = " and am_catecode like '" + othCatecode + "%'";
			} else {
				cond = " and am_catecode like '1221%'";
			}
			Double amount = baseDao.getFieldValue("assmonth", "sum(am_enddebit-am_endcredit)", "am_yearmonth=" + lastym + cond
					+ " and am_enddebit-am_endcredit>0", Double.class);
			List<Map<String, Object>> accountInforOthAR = new ArrayList<Map<String, Object>>();
			sql = "select * from (select am_asstype,am_assname,amount amount from(select am_asstype,am_assname,sum(am_enddebit-am_endcredit) amount from assmonth where am_yearmonth="
					+ lastym + cond + " group by am_asstype,am_assname ) where AMOUNT<>0 order by amount desc ) where rownum < 6";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_caname", rs.getString("am_asstype"));
				map.put("ai_kind", "其他应收账款");
				map.put("ai_cuname", rs.getString("am_assname"));
				map.put("ai_amount", rs.getDouble("amount"));
				map.put("ai_billamount", amount);
				map.put("ai_rate", amount == 0 ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				accountInforOthAR.add(map);
			}
			result.put("accountinforothar", FlexJsonUtil.toJsonArray(accountInforOthAR));
		}
		if (accountinforpp != null && accountinforpp) {
			// 资产情况-预付账款
			Double amount = baseDao
					.getFieldValue(
							"prepay left join CURRENCYSMONTH on CM_CRNAME=pp_CURRENCY and CM_YEARMONTH=TO_CHAR(pp_date,'yyyymm')",
							"sum(round((round(pp_jsamount*case when pp_type='预付退款单' then -1 else 1 end,2)-round(nvl(pp_havebalance,0),2))*case when nvl(cm_crrate,0)=0 then 1 else cm_crrate end,2)) ",
							"pp_statuscode='POSTED'", Double.class);
			List<Map<String, Object>> accountInforPP = new ArrayList<Map<String, Object>>();
			sql = "select * from (select ve_shortname,ve_name,ve_payment,ve_bank,ve_bankaccount,ve_contact,ve_tel,ve_add1,amount from "
					+ "(select pp_vendcode,sum(round((round(pp_jsamount*case when pp_type='预付退款单' then -1 else 1 end,2)-round(nvl(pp_havebalance,0),2))*case when nvl(cm_crrate,0)=0 then 1 else cm_crrate end,2)) amount "
					+ "from prepay,CURRENCYSMONTH where CM_CRNAME=pp_CURRENCY and CM_YEARMONTH=TO_CHAR(pp_date,'yyyymm') and pp_statuscode='POSTED' group by pp_vendcode) left join vendor on pp_vendcode=ve_code where AMOUNT<>0 order by AMOUNT desc) where rownum < 6";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "预付账款");
				map.put("ai_cushortname", rs.getString("ve_shortname"));
				map.put("ai_cuname", rs.getString("ve_name"));
				map.put("ai_add", rs.getString("ve_add1"));
				map.put("ai_contact", rs.getString("ve_contact"));
				map.put("ai_tel", rs.getString("ve_tel"));
				map.put("ai_bank", rs.getString("ve_bank"));
				map.put("ai_bankno", rs.getString("ve_bankaccount"));
				map.put("ai_leftamount", rs.getDouble("amount"));
				map.put("ai_billamount", NumberUtil.nvl(amount, 0));
				map.put("ai_rate", (amount == 0 || amount == null) ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				map.put("ai_payment", rs.getString("ve_payment"));
				accountInforPP.add(map);
			}
			result.put("accountinforpp", FlexJsonUtil.toJsonArray(accountInforPP));
		}
		if (accountinforinv != null && accountinforinv) {
			// 资产情况-存货
			List<Map<String, Object>> accountInforInv = new ArrayList<Map<String, Object>>();
			Double amount = baseDao
					.getFieldValue(
							"catemonth left join category on cm_catecode=ca_code",
							"SUM(nvl(cm_enddebit,0)-nvl(cm_endcredit,0))",
							"cm_yearmonth="
									+ lastym
									+ " and (cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount!scm', 'stockCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('CheckAccount!COST', 'DirectMaterialsCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('CheckAccount!COST', 'ProcessingCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount', 'gsCatecode'), chr(10))))) ",
							Double.class);
			sql = "select cm_catecode,ca_description,nvl(cm_enddebit,0)-nvl(cm_endcredit,0) amount from catemonth,category where cm_catecode=ca_code and cm_yearmonth="
					+ lastym
					+ " and (cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount!scm', 'stockCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('CheckAccount!COST', 'DirectMaterialsCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('CheckAccount!COST', 'ProcessingCatecode'), chr(10)))) or cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount', 'gsCatecode'), chr(10)))))";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "存货");
				map.put("ai_caname", rs.getString("ca_description"));
				map.put("ai_amount", rs.getDouble("amount"));
				map.put("ai_billamount", NumberUtil.nvl(amount, 0)); // 欠款总额
				map.put("ai_rate", (amount == 0 || amount == null) ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				accountInforInv.add(map);
			}
			result.put("accountinforinv", FlexJsonUtil.toJsonArray(accountInforInv));
		}
		if (accountinforfix != null && accountinforfix) {
			// 资产情况-固定资产
			Double amount = baseDao
					.getFieldValue(
							"catemonth left join category on cm_catecode=ca_code",
							"SUM(nvl(cm_enddebit,0)-nvl(cm_endcredit,0))",
							"cm_yearmonth="
									+ lastym
									+ " and cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount!AS', 'fixCatecode'), chr(10))))",
							Double.class);
			List<Map<String, Object>> accountInforFix = new ArrayList<Map<String, Object>>();
			sql = "select cm_catecode,ca_description,nvl(cm_enddebit,0)-nvl(cm_endcredit,0) amount from catemonth,category where cm_catecode=ca_code and cm_yearmonth="
					+ lastym
					+ " and cm_catecode in (select column_value from table(parsestring(getconfig('MonthAccount!AS', 'fixCatecode'), chr(10))))";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "固定资产");
				map.put("ai_caname", rs.getString("ca_description"));
				map.put("ai_amount", rs.getDouble("amount"));
				map.put("ai_billamount", NumberUtil.nvl(amount, 0)); // 欠款总额
				map.put("ai_rate", (amount == 0 || amount == null) ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				accountInforFix.add(map);
			}
			result.put("accountinforfix", FlexJsonUtil.toJsonArray(accountInforFix));
		}
		if (accountinforlb != null && accountinforlb) {
			// 负责情况-短期借款-贷款银行
			List<Map<String, Object>> accountInforLB = new ArrayList<Map<String, Object>>();
			sql = "select ca_description,cm_endcredit-cm_enddebit monthamount,cm_umendcredit-cm_umenddebit ummonthamount from catemonth left join category on cm_catecode=ca_code "
					+ "where ca_description like '短期借款%' and nvl(ca_isleaf,0)<>0 and cm_yearmonth=" + yearmonth + " order by cm_catecode";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "短期借款-贷款银行");
				map.put("ai_caname", rs.getString("ca_description"));
				map.put("ai_leftamount", rs.getDouble("monthamount"));
				map.put("ai_timebalance", rs.getDouble("ummonthamount"));
				accountInforLB.add(map);
			}
			result.put("accountinforlb", FlexJsonUtil.toJsonArray(accountInforLB));
		}
		if (accountinforap != null && accountinforap) {
			// 负责情况-应付账款
			Double amount = baseDao
					.getFieldValue(
							"APBILL left join CURRENCYSMONTH on CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm')",
							"SUM(ROUND((ROUND(ab_apamount,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2))",
							"AB_STATUSCODE='POSTED'", Double.class);
			List<Map<String, Object>> accountInforAP = new ArrayList<Map<String, Object>>();
			List<Map<String, Object>> accountInforAPDet = new ArrayList<Map<String, Object>>();
			sql = "select * from (select ve_shortname,ve_name,ve_contact,ve_tel,ve_add1,ve_payment,amount from "
					+ "(select ab_vendcode,SUM(ROUND((ROUND(ab_apamount,2)-ROUND(NVL(ab_payamount,0),2))*case when NVL(CM_CRRATE,0)=0 then 1 else CM_CRRATE end,2)) AMOUNT "
					+ "from APBILL,CURRENCYSMONTH where CM_CRNAME=AB_CURRENCY and CM_YEARMONTH=TO_CHAR(AB_DATE,'yyyymm') "
					+ "and AB_STATUSCODE='POSTED' group by ab_vendcode) left join vendor on ab_vendcode=ve_code where AMOUNT<>0 order by AMOUNT desc) where rownum < 6";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				int ai_id = baseDao.getSeqId("AL_ACCOUNTINFOR_SEQ");
				map.put("ai_id", ai_id);
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "应付账款");
				map.put("ai_cushortname", rs.getString("ve_shortname"));
				map.put("ai_cuname", rs.getString("ve_name"));
				map.put("ai_add", rs.getString("ve_add1"));
				map.put("ai_contact", rs.getString("ve_contact"));
				map.put("ai_tel", rs.getString("ve_tel"));
				map.put("ai_leftamount", rs.getDouble("amount"));
				map.put("ai_billamount", NumberUtil.nvl(amount, 0)); // 欠款总额
				map.put("ai_rate", (amount == 0 || amount == null) ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				map.put("ai_payment", rs.getString("ve_payment"));
				accountInforAP.add(map);
				// 欠款明细
				String sql1 = "select years,amount,pamount,case when days>0 then '是' else '否' end overdue from "
						+ "(select sum(ab_apamount) amount, to_char(ab_date,'yyyy') years,sum(ab_apamount-ab_payamount) pamount,sum(ab_actpaydate-ab_paydate) days "
						+ "from apbill left join vendor on ab_vendcode=ve_code where ve_name='" + rs.getString("ve_name")
						+ "' and ab_statuscode='POSTED' " + "group by to_char(ab_date,'yyyy') order by to_char(ab_date,'yyyy'))";
				SqlRowList rs1 = baseDao.queryForRowSet(sql1);
				while (rs1.next()) {
					Map<String, Object> map1 = new HashMap<String, Object>();
					map1.put("aid_id", baseDao.getSeqId("AL_ACCOUNTINFORDETAIL_SEQ"));
					map1.put("aid_aiid", ai_id);
					map1.put("aid_detno", rs1.getCurrentIndex() + 1);
					map1.put("aid_kind", "应付账款");
					map1.put("aid_year", rs1.getInt("years"));
					map1.put("aid_amount", rs1.getDouble("amount"));
					map1.put("aid_chargeamount", rs1.getDouble("pamount"));
					map1.put("aid_overdue", rs1.getString("overdue"));
					accountInforAPDet.add(map1);
				}
			}
			result.put("accountinforap", FlexJsonUtil.toJsonArray(accountInforAP));
			result.put("accountinforapdet", FlexJsonUtil.toJsonArray(accountInforAPDet));
		}
		if (accountinforothap != null && accountinforothap) {
			// 资产情况-其他应付账款
			String othCatecode = baseDao.getDBSetting("MonthAccount!AP", "othCatecode");
			String cond = " ";
			if (!StringUtils.isEmpty(othCatecode)) {
				cond = " and am_catecode like '" + othCatecode + "%'";
			} else {
				cond = " and am_catecode like '2241%'";
			}
			Double amount = baseDao.getFieldValue("assmonth", "sum(am_endcredit-am_enddebit)", "am_yearmonth=" + lastym + cond
					+ " and am_endcredit-am_enddebit>0", Double.class);
			List<Map<String, Object>> accountInforOthAP = new ArrayList<Map<String, Object>>();
			sql = "select * from (select am_asstype,am_assname,amount amount from(select am_asstype,am_assname,sum(am_endcredit-am_enddebit) amount from assmonth where am_yearmonth="
					+ lastym + cond + " group by am_asstype,am_assname ) where AMOUNT<>0 order by amount desc ) where rownum < 6";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_caname", rs.getString("am_asstype"));
				map.put("ai_kind", "其他应付账款");
				map.put("ai_cuname", rs.getString("am_assname"));
				map.put("ai_amount", rs.getDouble("amount"));
				map.put("ai_billamount", amount);
				map.put("ai_rate", amount == 0 ? 0 : NumberUtil.formatDouble(rs.getDouble("amount") / amount * 100, 2));
				accountInforOthAP.add(map);
			}
			result.put("accountinforothap", FlexJsonUtil.toJsonArray(accountInforOthAP));
		}
		if (accountinforlong != null && accountinforlong) {
			// 负责情况-长期借款
			List<Map<String, Object>> accountInforLong = new ArrayList<Map<String, Object>>();
			sql = "select ca_description,cm_endcredit-cm_enddebit monthamount,cm_umendcredit-cm_umenddebit ummonthamount from catemonth left join category on cm_catecode=ca_code "
					+ "where ca_description like '长期借款%' and nvl(ca_isleaf,0)<>0 and cm_yearmonth=" + lastym + " order by cm_catecode";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("ai_detno", rs.getCurrentIndex() + 1);
				map.put("ai_kind", "长期借款");
				map.put("ai_caname", rs.getString("ca_description"));
				map.put("ai_leftamount", rs.getDouble("monthamount"));
				map.put("ai_timebalance", rs.getDouble("ummonthamount"));
				accountInforLong.add(map);
			}
			result.put("accountinforlong", FlexJsonUtil.toJsonArray(accountInforLong));
		}
		// 经营情况-前五大上下游客户
		if (updowncust != null && updowncust) {
			List<Map<String, Object>> Updowncust = new ArrayList<Map<String, Object>>();
			// 前五大上游客户
			sql = "SELECT PU_VENDCODE,PU_VENDNAME,VE_BUSINESSRANGE,TOTAL,VE_PAYMENT FROM (SELECT PU_VENDCODE,PU_VENDNAME,VE_BUSINESSRANGE,TOTAL,VE_PAYMENT FROM "
					+ "(SELECT PU_VENDCODE,PU_VENDNAME,ROUND(SUM(NVL(PU_TOTAL,0)*NVL(PU_RATE,1)),2) TOTAL FROM PURCHASE "
					+ "WHERE PU_STATUSCODE = 'AUDITED' AND TO_CHAR(PU_DATE,'yyyy') ="
					+ (year - 1)
					+ " GROUP BY PU_VENDCODE,PU_VENDNAME) LEFT JOIN VENDOR ON PU_VENDCODE = VE_CODE ORDER BY TOTAL DESC) WHERE ROWNUM <=5";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Double amount = baseDao
						.getFieldValue("PURCHASE", "ROUND(SUM(NVL(PU_TOTAL,0)*NVL(PU_RATE,1)),2)",
								"PU_STATUSCODE='AUDITED' and pu_vendcode='" + rs.getString("PU_VENDCODE")
										+ "' AND TO_CHAR(PU_DATE,'yyyy')=" + year, Double.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("udc_detno", rs.getCurrentIndex() + 1);
				map.put("udc_kind", "上游");
				map.put("udc_name", rs.getString("PU_VENDNAME"));
				map.put("udc_product", rs.getString("VE_BUSINESSRANGE"));
				map.put("udc_lastyear", rs.getGeneralDouble("TOTAL"));
				map.put("udc_thisyear", amount);
				map.put("udc_payment", rs.getString("VE_PAYMENT"));
				Updowncust.add(map);
			}
			// 前五大下游客户
			sql = "SELECT SA_CUSTCODE,SA_CUSTNAME,CU_BUSINESSRANGE,TOTAL,CU_PAYMENTS FROM (SELECT SA_CUSTCODE,SA_CUSTNAME,CU_BUSINESSRANGE,TOTAL,CU_PAYMENTS "
					+ "FROM (SELECT SA_CUSTCODE,SA_CUSTNAME,ROUND(SUM(NVL(SA_TOTAL,0)*NVL(SA_RATE,1)),2) TOTAL FROM SALE WHERE SA_STATUSCODE = 'AUDITED' "
					+ "AND TO_CHAR(SA_DATE,'yyyy') ="
					+ (year - 1)
					+ " GROUP BY  SA_CUSTCODE,SA_CUSTNAME) "
					+ "LEFT JOIN CUSTOMER ON SA_CUSTCODE = CU_CODE ORDER BY TOTAL DESC) WHERE ROWNUM <=5";
			rs = baseDao.queryForRowSet(sql);
			while (rs.next()) {
				Double amount = baseDao
						.getFieldValue("SALE", "ROUND(SUM(NVL(SA_TOTAL,0)*NVL(SA_RATE,1)),2)", "SA_STATUSCODE='AUDITED' and SA_CUSTCODE='"
								+ rs.getString("SA_CUSTCODE") + "' AND TO_CHAR(SA_DATE,'yyyy')=" + year, Double.class);
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("udc_detno", rs.getCurrentIndex() + 1);
				map.put("udc_kind", "下游");
				map.put("udc_name", rs.getString("SA_CUSTNAME"));
				map.put("udc_product", rs.getString("CU_BUSINESSRANGE"));
				map.put("udc_lastyear", rs.getGeneralDouble("TOTAL"));
				map.put("udc_thisyear", amount);
				map.put("udc_payment", rs.getString("CU_PAYMENTS"));
				Updowncust.add(map);
			}
			result.put("updowncust", FlexJsonUtil.toJsonArray(Updowncust));
		}

		return result;
	}

	@Override
	public void recBalanceAssign(String mfcusts) {
		try {
			mfcusts = URLDecoder.decode(mfcusts, "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		List<Map<Object, Object>> Mfcusts = BaseUtil.parseGridStoreToMaps(mfcusts);
		Object cqcode = Mfcusts.get(0).get("cuc_cqcode");
		String applyman = baseDao.getFieldValue("FINANCINGAPPLY", "FA_APPLYMAN", "FA_BUSINCODE = '" + cqcode +"'", String.class);
		String fcname = baseDao.getFieldValue("FINANCECORPORATION", "FC_NAME", "FC_CODE ='" + Mfcusts.get(0).get("cuc_fccode") + "'", String.class);
		List<String> sqls = new ArrayList<String>();
		for (Map<Object, Object> mfcust : Mfcusts) {
			Object mfcustcode = mfcust.get("cuc_custcode");
			if (!StringUtil.hasText(mfcustcode)) {
				Object mfcustname = mfcust.get("cuc_custname");
				mfcustcode = baseDao.getFieldDataByCondition("Customer", "cu_code", "cu_name = '" + mfcustname + "'");
				mfcust.put("cuc_custcode", mfcustcode);
			}
			int cucid = baseDao.getSeqId("FSCUSTOMERCREDIT_SEQ");
			mfcust.put("cuc_id", cucid);
			if (applyman!=null) {
				mfcust.put("cuc_recorder", applyman);
			}
			mfcust.put("cuc_remaincredit", mfcust.get("cuc_credit"));
			mfcust.put("cuc_usecredit", 0);
			mfcust.put("cuc_indate", DateUtil.format(new Date(), Constant.YMD_HMS));
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(new Date());
			calendar.add(Calendar.MONTH, 12);
			mfcust.put("cuc_enddate", DateUtil.format(calendar.getTime(), Constant.YMD_HMS));
			mfcust.put("cuc_status", "有效");
			mfcust.put("cuc_statuscode", "VALID");
			mfcust.put("cuc_fcname", fcname);
			sqls.add("update FSCUSTOMERCREDIT set cuc_status ='无效',cuc_statuscode='UNVALID' where cuc_custcode = '"+mfcustcode+"' and cuc_fcname = '"+fcname+"' and cuc_statuscode = 'VALID'");
			///sqls.add("update FSCUSTOMERCREDIT set cuc_status ='无效',cuc_statuscode='UNVALID' where cuc_custcode = '"+mfcustcode+"' and cuc_cqcode = '" + cqcode + "' and cuc_statuscode = 'VALID'");
		}
		sqls.addAll(SqlUtil.getInsertSqlbyGridStore(Mfcusts, "FSCUSTOMERCREDIT"));
		baseDao.execute(sqls);
		// 知会申请人进行应收账款转让
		if (applyman!=null) {
			Employee employee = employeeDao.getEmployeeByConditon("em_name='" +applyman + "' and nvl(em_class, ' ')<>'离职'");

			if (employee != null) {
				String context = "您向<font color=\"#0000ff\">" + fcname + "</font>进行的保理额度申请，保理额度已经审批，请及时进行应收账款转让";
				baseDao.pagingRelease("金融知会消息", context, employee, true);
			}
		}
	}

	@Override
	public void accountApply(String custcode, String custname, String amount, String cqcode) {
		String condition = "CUC_CUSTCODE = '"+custcode+"'";
		if (!StringUtil.hasText(custcode)) {
			condition = "CUC_CUSTNAME = '"+custname+"'";
		}
		condition += " AND CUC_CQCODE = '"+cqcode+"'";
		baseDao.updateByCondition("FSCUSTOMERCREDIT", "cuc_usecredit = nvl(cuc_usecredit,0)+"+StringUtil.nvl(amount, "0"), condition+" and CUC_STATUSCODE = 'VALID'");
		baseDao.updateByCondition("FSCUSTOMERCREDIT", "cuc_remaincredit = nvl(cuc_credit,0)-nvl(cuc_usecredit,0)", condition+" and CUC_STATUSCODE = 'VALID'");
	}

	@Override
	public List<Map<String, Object>> getCustSaleReportProgress(String ordercode) {
		String res = baseDao.callProcedure("SP_SALESTATUS", new Object[]{ordercode});
		if (!StringUtil.hasText(res)||!"OK".equals(res)) {
			BaseUtil.showError(res);
		}
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		
		SqlRowList rs  = baseDao.queryForRowSet("SELECT SR_DESCRIPTION,SR_DATE,SR_ISOK FROM SALEREPORT WHERE SR_CODE = ? ORDER BY SR_DETNO,SR_DATE",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("desc", rs.getString("SR_DESCRIPTION"));
			if (rs.getDate("SR_DATE")==null) {
				map.put("date","");
			}else {
				map.put("date", DateUtil.format(rs.getDate("SR_DATE"), "yyyy年MM月dd日"));
			}
			map.put("isok", rs.getGeneralInt("SR_ISOK")==1?"greencircle":"redcircle");
			data.add(map);
		}
		return data;
	}
	
	@Override
	public Map<String, Object> getCustSaleReportDetail(String ordercode) {
		Map<String, Object> result = new HashMap<String,Object>();
		result.put("order", getSaleOrder(ordercode));
		result.put("deposit", getPreRec(ordercode));
		result.put("purchase", getPurchase(ordercode));
		result.put("make", getMake(ordercode));
		result.put("accept", getAccept(ordercode));
		result.put("saleout", getSaleOut(ordercode));
		result.put("payforAR", getPayforAR(ordercode));
		return result;
	}
	
	private Map<String, Object> getSaleOrder(String ordercode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, Object> sale = new HashMap<String, Object>();
		List<Map<String, Object>> saleDetail = new ArrayList<Map<String,Object>>();
		SqlRowList rs = null;
		Object[] sales = baseDao.getFieldsDataByCondition("Sale", "sa_custname,sa_kind,sa_currency,sa_seller,"
				+ "sa_toplace,sa_transport,sa_total,sa_totalupper", "sa_code='"+ordercode+"'");
		if (sales!=null) {
			sale.put("sa_custname", sales[0]);
			sale.put("sa_kind", sales[1]);
			sale.put("sa_currency", sales[2]);
			sale.put("sa_seller", sales[3]);
			sale.put("sa_toplace", sales[4]);
			sale.put("sa_transport", sales[5]);
			modelMap.put("sa_total", sales[6]);
			modelMap.put("sa_totalupper", sales[7]);
		}
		
		modelMap.put("form", sale);
		rs = baseDao.queryForRowSet("select * from SaleDetail inner join Sale on sd_said = sa_id left join "
				+ "Product on sd_prodid = pr_id where sa_code = ?",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sd_detno", rs.getGeneralInt("sd_detno"));
			map.put("sd_prodcode", rs.getString("sd_prodcode"));
			map.put("pr_spec", rs.getString("pr_spec"));
			map.put("pr_unit", rs.getString("pr_unit"));
			map.put("sd_price", rs.getDouble("sd_price"));
			map.put("sd_qty", rs.getGeneralInt("sd_qty"));
			map.put("sd_taxrate", rs.getDouble("sd_taxrate"));
			map.put("sd_total", rs.getDouble("sd_total"));
			if (rs.getDate("sd_delivery")==null) {
				map.put("sd_delivery", "");
			}else {
				map.put("sd_delivery", DateUtil.format(rs.getDate("sd_delivery"),Constant.YMD));
			}
			
			map.put("sd_remark", rs.getString("sd_remark"));
			map.put("pr_detail", rs.getString("pr_detail"));
			saleDetail.add(map);
		}
		modelMap.put("data", saleDetail);
		return modelMap;
	}

	private Map<String, Object> getPreRec(String ordercode) {
		Map<String, Object> data = new HashMap<String, Object>();
		SqlRowList rs = baseDao.queryForRowSet("select Round(prd_orderamount,2) prd_orderamount,Round(prd_nowbalance,2) prd_nowbalance,"
				+ "Round(prd_nowbalance/prd_orderamount*100,2) prd_ratio,pr_date,pr_accountname from "
				+ "PreRecDetail left join PreRec on prd_prid=pr_id where prd_ordercode=?",ordercode);
		if (rs.next()) {
			data.put("prd_orderamount", rs.getDouble("prd_orderamount"));
			data.put("prd_nowbalance", rs.getDouble("prd_nowbalance"));
			data.put("prd_ratio", rs.getDouble("prd_ratio"));
			if (rs.getDate("pr_date")!=null) {
				data.put("pr_date", DateUtil.format(rs.getDate("pr_date"), Constant.YMD));
			}else {
				data.put("pr_date", "");
			}
			data.put("pr_accountname", rs.getString("pr_accountname"));
		}
		return data;
	}

	private List<Map<String, Object>> getPurchase(String ordercode) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select sa_code,sd_prodcode,sd_qty,round(sd_price,2) sd_price,sd_pmcremark,sd_mrpstatus from sale "
				+ "left join saledetail on sa_id=sd_said where nvl(sd_pmcremark,' ')<>' ' and sa_code=?",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("sa_code", rs.getString("sa_code"));
			map.put("sd_prodcode", rs.getString("sd_prodcode"));
			map.put("sd_qty", rs.getGeneralInt("sd_qty"));
			map.put("sd_price", rs.getDouble("sd_price"));
			map.put("sd_pmcremark", rs.getString("sd_pmcremark"));
			map.put("sd_mrpstatus", rs.getString("sd_mrpstatus"));
			data.add(map);
		}
		return data;
	}

	private List<Map<String, Object>> getMake(String ordercode) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select ma_code,ma_prodcode,ma_qty,round(ma_price,2) ma_price,ma_planbegindate,ma_planenddate from "
				+ "make where ma_tasktype='MAKE' and ma_salecode=?",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("ma_code", rs.getString("ma_code"));
			map.put("ma_prodcode", rs.getString("ma_prodcode"));
			map.put("ma_qty", rs.getGeneralInt("ma_qty"));
			map.put("ma_price", rs.getDouble("ma_price"));
			if (rs.getDate("ma_planbegindate")!=null) {
				map.put("ma_planbegindate", DateUtil.format(rs.getDate("ma_planbegindate"),Constant.YMD));
			}else {
				map.put("ma_planbegindate", "");
			}
			if (rs.getDate("ma_planenddate")!=null) {
				map.put("ma_planenddate", DateUtil.format(rs.getDate("ma_planenddate"),Constant.YMD));
			}else {
				map.put("ma_planenddate", "");
			}
			data.add(map);
		}
		return data;
	}

	private List<Map<String, Object>> getAccept(String ordercode) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pd_prodcode,pd_inqty,round(pd_price,2) pd_price,pd_prodmadedate,pi_date from "
				+ "prodiodetail left join prodinout on pd_piid=pi_id left join make on pd_ordercode=ma_code where pi_class='完工入库单' and ma_salecode=?",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pi_inoutno", rs.getString("pi_inoutno"));
			map.put("pd_prodcode", rs.getString("pd_prodcode"));
			map.put("pd_inqty", rs.getGeneralInt("pd_inqty"));
			map.put("pd_price", rs.getDouble("pd_price"));
			if (rs.getDate("pd_prodmadedate")!=null) {
				map.put("pd_prodmadedate", DateUtil.format(rs.getDate("pd_prodmadedate"),Constant.YMD));
			}else {
				map.put("pd_prodmadedate", "");
			}
			if (rs.getDate("pi_date")!=null) {
				map.put("pi_date", DateUtil.format(rs.getDate("pi_date"),Constant.YMD));
			}else {
				map.put("pi_date", "");
			}
			data.add(map);
		}
		return data;
	}

	private List<Map<String, Object>> getSaleOut(String ordercode) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		SqlRowList rs = baseDao.queryForRowSet("select pi_inoutno,pd_prodcode,pd_outqty,round(pd_sendprice,2) pd_sendprice,round(pd_price,2) "
				+ "pd_price,pi_date from PRODIODETAIL LEFT JOIN PRODINOUT ON PD_PIID=PI_ID where pi_class='出货单' and pd_ordercode=?",ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("pi_inoutno", rs.getString("pi_inoutno"));
			map.put("pd_prodcode", rs.getString("pd_prodcode"));
			map.put("pd_outqty", rs.getGeneralInt("pd_inqty"));
			map.put("pd_sendprice", rs.getDouble("pd_sendprice"));
			map.put("pd_price", rs.getDouble("pd_price"));
			if (rs.getDate("pi_date")!=null) {
				map.put("pi_date", DateUtil.format(rs.getDate("pi_date"),Constant.YMD));
			}else {
				map.put("pi_date", "");
			}
			data.add(map);
		}
		return data;
	}

	private Map<String, Object> getPayforAR(String ordercode) {
		List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String sql = "";
		boolean useBillOutAR = baseDao.isDBSetting("useBillOutAR");
		if (useBillOutAR) {
			sql = "select bi_code,ard_prodcode,ard_nowqty,round(ard_nowprice,2) ard_nowprice,round(ard_costprice,2) ard_costprice,bi_date from "
					+ "BilloutDetail left join Billout on ard_biid = bi_id left join ArbillDetail on ard_ordercode=abd_code and ard_orderdetno=abd_detno where abd_ordercode = ?";
			modelMap.put("useBillOutAR", 1);
		}else {
			sql = "select ab_code,abd_prodcode,abd_thisvoqty,round(abd_price,2) abd_price,round(abd_costprice,2) abd_costprice,ab_date from "
					+ "ArbillDetail left join Arbill on abd_abid = ab_id where abd_ordercode = ?";
			modelMap.put("useBillOutAR", 0);
		}
		
		SqlRowList rs = baseDao.queryForRowSet(sql,ordercode);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			if (useBillOutAR) {
				map.put("bi_code", rs.getString("bi_code"));
				map.put("ard_prodcode", rs.getString("ard_prodcode"));
				map.put("ard_nowqty", rs.getGeneralInt("ard_nowqty"));
				map.put("ard_nowprice", rs.getDouble("ard_nowprice"));
				map.put("ard_costprice", rs.getDouble("ard_costprice"));
				if (rs.getDate("bi_date")!=null) {
					map.put("bi_date", DateUtil.format(rs.getDate("bi_date"),Constant.YMD));
				}else {
					map.put("bi_date", "");
				}
			}else {
				map.put("ab_code", rs.getString("ab_code"));
				map.put("abd_prodcode", rs.getString("abd_prodcode"));
				map.put("abd_thisvoqty", rs.getGeneralInt("abd_thisvoqty"));
				map.put("abd_price", rs.getDouble("abd_price"));
				map.put("abd_costprice", rs.getDouble("abd_costprice"));
				if (rs.getDate("ab_date")!=null) {
					map.put("ab_date", DateUtil.format(rs.getDate("ab_date"),Constant.YMD));
				}else {
					map.put("ab_date", rs.getDate(""));
				}
			}
			
			data.add(map);
		}
		modelMap.put("data", data);
		return modelMap;
	}
	
}

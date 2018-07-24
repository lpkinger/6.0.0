package com.uas.erp.service.fa.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.StringUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.service.fa.CheckAccountService;
import com.uas.erp.service.fa.MonthAccountService;

@Service
public class MonthAccountServiceImpl implements MonthAccountService {

	@Autowired
	private BaseDao baseDao;

	@Autowired
	private VoucherDao voucherDao;

	@Autowired
	private CheckAccountService checkAccountService;

	final static String AR = "SELECT '应收' am_catecode, custcode am_asscode, max(cu_name) am_assname, currency am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(cm_beginamount,2)) cm_beginamount, 0 cm_umnowamount, 0 cm_umendamount, sum(round(cm_nowamount,2)) cm_nowamount, sum(round(cm_payamount,2)) cm_payamount, sum(round(cm_endamount,2)) cm_endamount, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit,sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit FROM ( SELECT (CASE WHEN NVL(cm_custcode,' ')=' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, nvl(am_doublebegindebit,0) am_doublebegindebit , nvl(am_doublebegincredit,0) am_doublebegincredit, nvl(am_umdoublenowdebit,0) am_umdoublenowdebit, nvl(am_umdoublenowcredit,0) am_umdoublenowcredit, nvl(am_doublenowdebit,0) am_doublenowdebit, nvl(am_doublenowcredit,0) am_doublenowcredit, nvl(cm_beginamount,0) cm_beginamount, nvl(cm_nowamount,0) cm_nowamount, nvl(cm_payamount,0) cm_payamount, nvl(cm_endamount,0) cm_endamount, nvl(am_doubleenddebit,0) am_doubleenddebit, nvl(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM  CustMonth full join (select sum(am_doublebegindebit) am_doublebegindebit ,sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(am_umdoubleenddebit) am_umdoubleenddebit, sum(am_umdoubleendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth,am_currency from AssMonth where am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency ) LEFT JOIN CUSTOMER ON custcode = cu_code WHERE yearmonth = ?  group by custcode, currency order by custcode";
	final static String AR1 = "select '应收' AM_CATECODE,CUSTCODE AM_ASSCODE,CU_NAME AM_ASSNAME,CURRENCY AM_CURRENCY,SUM(ROUND(AM_DOUBLEBEGINDEBIT,2)) AM_DOUBLEBEGINDEBIT,SUM(ROUND(AM_DOUBLEBEGINCREDIT,2)) AM_DOUBLEBEGINCREDIT,SUM(ROUND(AM_UMDOUBLENOWDEBIT,2)) AM_UMDOUBLENOWDEBIT,SUM(ROUND(AM_UMDOUBLENOWCREDIT,2)) AM_UMDOUBLENOWCREDIT, SUM(ROUND(AM_DOUBLENOWDEBIT,2)) AM_DOUBLENOWDEBIT,SUM(ROUND(AM_DOUBLENOWCREDIT,2)) AM_DOUBLENOWCREDIT, SUM(ROUND(CM_BEGINAMOUNT-CM_GSBEGINAMOUNTS,2)) CM_BEGINAMOUNT, 0 CM_UMNOWAMOUNT, 0 CM_UMENDAMOUNT,SUM(ROUND(CM_NOWAMOUNT-CM_GSNOWAMOUNTS+CM_GSINVOAMOUNTS,2)) CM_NOWAMOUNT, SUM(ROUND(cm_payamount,2)) CM_PAYAMOUNT,SUM(ROUND(CM_ENDAMOUNT-CM_GSENDAMOUNTS,2)) CM_ENDAMOUNT, SUM(ROUND(AM_DOUBLEENDDEBIT,2)) AM_DOUBLEENDDEBIT,SUM(ROUND(AM_DOUBLEENDCREDIT,2)) AM_DOUBLEENDCREDIT, SUM(ROUND(AM_UMDOUBLEENDDEBIT,2)) AM_UMDOUBLEENDDEBIT,SUM(ROUND(AM_UMDOUBLEENDCREDIT,2)) AM_UMDOUBLEENDCREDIT from (select NVL2(CM_CUSTCODE,CM_CUSTCODE,AM_ASSCODE) CUSTCODE,NVL2(CM_CURRENCY,CM_CURRENCY,AM_CURRENCY) CURRENCY,NVL2(CM_YEARMONTH,CM_YEARMONTH,AM_YEARMONTH) YEARMONTH,NVL(AM_DOUBLEBEGINDEBIT,0) AM_DOUBLEBEGINDEBIT,NVL(AM_DOUBLEBEGINCREDIT,0) AM_DOUBLEBEGINCREDIT,NVL(AM_UMDOUBLENOWDEBIT,0) AM_UMDOUBLENOWDEBIT,NVL(AM_UMDOUBLENOWCREDIT,0) AM_UMDOUBLENOWCREDIT,NVL(AM_DOUBLENOWDEBIT,0) AM_DOUBLENOWDEBIT,NVL(AM_DOUBLENOWCREDIT,0) AM_DOUBLENOWCREDIT,NVL(CM_BEGINAMOUNT,0) CM_BEGINAMOUNT,NVL(CM_NOWAMOUNT,0) CM_NOWAMOUNT,NVL(CM_PAYAMOUNT,0) CM_PAYAMOUNT,NVL(CM_ENDAMOUNT,0) CM_ENDAMOUNT,NVL(CM_GSBEGINAMOUNTS,0) CM_GSBEGINAMOUNTS,NVL(CM_GSNOWAMOUNTS,0) CM_GSNOWAMOUNTS,NVL(CM_GSINVOAMOUNTS,0) CM_GSINVOAMOUNTS,NVL(CM_GSENDAMOUNTS,0) CM_GSENDAMOUNTS,NVL(AM_DOUBLEENDDEBIT,0) AM_DOUBLEENDDEBIT,NVL(AM_DOUBLEENDCREDIT,0) AM_DOUBLEENDCREDIT,NVL(AM_UMDOUBLEENDDEBIT,0) AM_UMDOUBLEENDDEBIT,NVL(AM_UMDOUBLEENDCREDIT,0) AM_UMDOUBLEENDCREDIT from CUSTMONTH full join (select SUM(AM_DOUBLEBEGINDEBIT) AM_DOUBLEBEGINDEBIT,SUM(AM_DOUBLEBEGINCREDIT) AM_DOUBLEBEGINCREDIT,SUM(AM_UMDOUBLENOWDEBIT) AM_UMDOUBLENOWDEBIT,SUM(AM_UMDOUBLENOWCREDIT) AM_UMDOUBLENOWCREDIT,SUM(AM_DOUBLENOWDEBIT) AM_DOUBLENOWDEBIT,SUM(AM_DOUBLENOWCREDIT) AM_DOUBLENOWCREDIT,SUM(AM_DOUBLEENDDEBIT) AM_DOUBLEENDDEBIT,SUM(AM_DOUBLEENDCREDIT) AM_DOUBLEENDCREDIT,SUM(AM_UMDOUBLEENDDEBIT) AM_UMDOUBLEENDDEBIT,SUM(AM_UMDOUBLEENDCREDIT) AM_UMDOUBLEENDCREDIT,AM_ASSCODE,AM_YEARMONTH,AM_CURRENCY from ASSMONTH where AM_CATECODE in (@CODE) and AM_ASSTYPE='客户往来' group by AM_YEARMONTH,AM_ASSCODE,AM_CURRENCY) on CM_YEARMONTH=AM_YEARMONTH and CM_CUSTCODE=AM_ASSCODE and CM_CURRENCY=AM_CURRENCY) left join CUSTOMER on CUSTCODE=CU_CODE where YEARMONTH=? group by CUSTCODE,CU_NAME,CURRENCY order by CUSTCODE";
	final static String AR2 = "select '应收' am_catecode, '' am_asscode, '' am_assname, currency am_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(cm_currency,' ')=' ' THEN cmc_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN cmc_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round( NVL(cm_beginamount, 0),2)) cm_beginamount,sum(round( NVL(cm_nowamount, 0),2)) cm_nowamount,sum(round( NVL(cm_payamount, 0),2)) cm_payamount,sum(round( NVL(cm_endamount, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on cm_yearmonth = cmc_yearmonth and cm_currency = cmc_currency) where yearmonth = ? order by currency";
	final static String AR3 = "select '应收' am_catecode, '' am_asscode, '' am_assname, currency am_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select NVL2(cm_currency,cm_currency,cmc_currency) currency,NVL2(cm_yearmonth,cm_yearmonth,cmc_yearmonth) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(cm_beginamount, 0)-NVL(cm_gsbeginamounts, 0),2)) cm_beginamount,sum(round(NVL(cm_nowamount,0)-NVL(cm_gsnowamounts,0)+NVL(cm_gsinvoamounts,0),2)) cm_nowamount,sum(round(NVL(cm_payamount,0),2)) cm_payamount,sum(round( NVL(cm_endamount, 0)-NVL(cm_gsendamounts, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on cm_yearmonth = cmc_yearmonth and cm_currency = cmc_currency) where yearmonth = ? order by currency";

	final static String AR_PRE = "SELECT '预收' am_catecode, custcode am_asscode, max(cu_name) am_assname, currency am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(cm_prepaybegin,2)) cm_beginamount, 0 cm_umnowamount, 0 cm_umendamount, sum(round(cm_prepaybalance,2)) cm_nowamount, sum(round(cm_prepaynow,2)) cm_payamount, sum(round(cm_prepayend,2)) cm_endamount, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit,sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit FROM ( SELECT (CASE WHEN NVL(cm_custcode,' ')=' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(cm_prepaybegin,0) cm_prepaybegin, NVL(cm_prepaynow,0) cm_prepaynow, NVL(cm_prepaybalance,0) cm_prepaybalance, NVL(cm_prepayend,0) cm_prepayend, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM CustMonth full join (select sum(am_doublebegindebit) am_doublebegindebit ,sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(am_umdoubleenddebit) am_umdoubleenddebit, sum(am_umdoubleendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth,am_currency from AssMonth where am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency ) LEFT JOIN CUSTOMER ON custcode = cu_code WHERE yearmonth = ? group by custcode, currency order by custcode ";
	final static String AR_PRE1 = "select '预收' am_catecode, '' am_asscode, '' am_assname, currency am_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(cm_currency,' ')=' ' THEN cmc_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN cmc_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round( NVL(cm_beginamount, 0),2)) cm_beginamount,sum(round( NVL(cm_nowamount, 0),2)) cm_nowamount,sum(round( NVL(cm_payamount, 0),2)) cm_payamount,sum(round( NVL(cm_endamount, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on cm_yearmonth = cmc_yearmonth and cm_currency = cmc_currency) where yearmonth = ? order by currency";

	final static String AR_GS = "select '发出商品' am_catecode, custcode am_asscode, max(cu_name) am_assname, 'RMB' am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit,sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit, sum(round(cm_gsbeginamount,2)) cm_beginamount, 0 cm_umnowamount, 0 cm_umendamount, sum(round(cm_gsnowamount,2)) cm_nowamount, sum(round(cm_gsinvoamount,2)) cm_payamount, sum(round(cm_gsendamount,2)) cm_endamount from (SELECT cm_currency,(CASE WHEN NVL(cm_custcode, ' ') = ' ' THEN am_asscode ELSE cm_custcode END) custcode, (CASE WHEN NVL(cm_yearmonth, 0) = 0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, NVL(am_doublebegindebit, 0) am_doublebegindebit, NVL(am_doublebegincredit, 0) am_doublebegincredit, NVL(am_umdoublenowdebit, 0) am_umdoublenowdebit, NVL(am_umdoublenowcredit, 0) am_umdoublenowcredit, NVL(am_doublenowdebit, 0) am_doublenowdebit, NVL(am_doublenowcredit, 0) am_doublenowcredit, NVL(am_doubleenddebit, 0) am_doubleenddebit, NVL(am_doubleendcredit, 0) am_doubleendcredit, NVL(cm_gsbeginamount, 0) cm_gsbeginamount, NVL(cm_gsnowamount, 0) cm_gsnowamount, NVL(cm_gsinvoamount, 0) cm_gsinvoamount, NVL(cm_gsendamount, 0) cm_gsendamount, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM custmonth full join (select sum(am_begindebit) am_doublebegindebit ,sum(am_begincredit) am_doublebegincredit, sum(am_umnowdebit) am_umdoublenowdebit, sum(am_umnowcredit) am_umdoublenowcredit, sum(am_nowdebit) am_doublenowdebit, sum(am_nowcredit) am_doublenowcredit, sum(am_enddebit) am_doubleenddebit, sum(am_endcredit) am_doubleendcredit, sum(am_umenddebit) am_umdoubleenddebit, sum(am_umendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth from assmonth where am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode) on cm_yearmonth=am_yearmonth and cm_custcode=am_asscode AND cm_currency=getconfig('sys','defaultCurrency')) LEFT JOIN CUSTOMER ON custcode = cu_code where yearmonth = ? group by custcode order by custcode";
	final static String AR_GS1 = "select '发出商品' am_catecode, '' am_asscode, '' am_assname,'RMB' am_currency,cm_beginamount,cm_nowamount,cm_payamount,cm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round( NVL(cm_gsbeginamount, 0),2)) cm_beginamount,sum(round( NVL(cm_gsnowamount, 0),2)) cm_nowamount,sum(round( NVL(cm_gsinvoamount, 0),2)) cm_payamount,sum(round( NVL(cm_gsendamount, 0),2)) cm_endamount,cm_yearmonth from custmonth group by cm_yearmonth) full join (select cm_yearmonth am_yearmonth,sum(round( NVL(cm_begindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cm_begincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cm_nowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cm_umnowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cm_nowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cm_umnowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cm_enddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cm_umenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cm_endcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cm_umendcredit, 0),2)) am_umdoubleendcredit from (select cm_yearmonth,cm_begindebit,cm_begincredit,cm_nowdebit,cm_umnowdebit,cm_nowcredit,cm_umnowcredit,cm_enddebit,cm_umenddebit,cm_endcredit,cm_umendcredit from catemonth where cm_catecode in (@CODE)) group by cm_yearmonth)  on cm_yearmonth = am_yearmonth) where yearmonth =?";

	final static String AR_SUM = "SELECT sum(round(cm_beginamount,2)), sum(round(nvl(am_doublebegindebit, 0),2) - round(nvl(am_doublebegincredit, 0),2)), sum(round(cm_nowamount,2)), sum(round(am_doublenowdebit,2)), sum(round(cm_payamount,2)), sum(round(am_doublenowcredit,2)), sum(round(cm_endamount,2)), sum(round(nvl(am_doubleenddebit, 0),2) - round(nvl(am_doubleendcredit, 0),2)), '应收' am_catecode, currency am_currency, sum(round(am_umdoublenowdebit,2)), sum(round(am_umdoublenowcredit,2)), sum(round(nvl(am_umdoublenowdebit, 0),2) - round(nvl(am_umdoublenowcredit, 0),2)), sum(round(nvl(am_umdoubleenddebit, 0),2) - round(nvl(am_umdoubleendcredit, 0),2)) am_umdoubleendamount FROM (SELECT (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, NVL(cm_beginamount,0) cm_beginamount, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(cm_nowamount,0) cm_nowamount, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(cm_payamount,0) cm_payamount, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(cm_endamount,0) cm_endamount, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM CustMonth full join (SELECT sum(am_doublebegindebit) am_doublebegindebit ,sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(am_umdoubleenddebit) am_umdoubleenddebit, sum(am_umdoubleendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth,am_currency FROM AssMonth WHERE  am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency) WHERE yearmonth = ? group by currency order by  currency";
	final static String AR_SUM1 = "SELECT sum(round(cm_beginamount-cm_gsbeginamounts,2)),sum(round(nvl(am_doublebegindebit,0),2) - round(nvl(am_doublebegincredit,0),2)),sum(round(cm_nowamount-cm_gsnowamounts+cm_gsinvoamounts,2)),sum(round(am_doublenowdebit,2)),sum(round(cm_payamount,2)),sum(round(am_doublenowcredit,2)),sum(round(cm_endamount-cm_gsendamounts,2)),sum(round(nvl(am_doubleenddebit,0),2)-round(nvl(am_doubleendcredit,0),2)), '应收' am_catecode,currency am_currency, sum(round(am_umdoublenowdebit,2)), sum(round(am_umdoublenowcredit,2)), sum(round(nvl(am_umdoublenowdebit, 0),2) - round(nvl(am_umdoublenowcredit, 0),2)), sum(round(nvl(am_umdoubleenddebit, 0),2) - round(nvl(am_umdoubleendcredit, 0),2)) am_umdoubleendamount FROM (SELECT NVL2(cm_currency,cm_currency,am_currency) currency,NVL2(cm_yearmonth,cm_yearmonth,am_yearmonth) yearmonth, NVL(cm_beginamount,0) cm_beginamount, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(cm_nowamount,0) cm_nowamount, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(cm_payamount,0) cm_payamount, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(cm_endamount,0) cm_endamount,nvl(cm_gsbeginamounts,0) cm_gsbeginamounts, nvl(cm_gsnowamounts,0) cm_gsnowamounts, nvl(cm_gsinvoamounts,0) cm_gsinvoamounts, nvl(cm_gsendamounts,0) cm_gsendamounts, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM CustMonth full join (SELECT sum(am_doublebegindebit) am_doublebegindebit ,sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(am_umdoubleenddebit) am_umdoubleenddebit, sum(am_umdoubleendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth,am_currency FROM AssMonth WHERE am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode,am_currency) on cm_yearmonth=am_yearmonth AND cm_custcode=am_asscode and cm_currency=am_currency) WHERE yearmonth=? group by currency order by currency";
	final static String AR_SUM2 = "select cm_beginamount,am_doublebegindebit-am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit-am_doubleendcredit,'应收' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowdebit-am_umdoublenowcredit,am_umdoubleenddebit-am_umdoubleendcredit from (select (CASE WHEN NVL(cm_currency,' ')=' ' THEN cmc_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN cmc_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round(NVL(cm_beginamount, 0),2)) cm_beginamount,sum(round( NVL(cm_nowamount, 0),2)) cm_nowamount,sum(round(NVL(cm_payamount, 0),2)) cm_payamount,sum(round(NVL(cm_endamount, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round(NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round(NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round(NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round(NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round(NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency)  on cm_yearmonth = cmc_yearmonth and cm_currency = cmc_currency) where yearmonth = ? order by currency";
	final static String AR_SUM3 = "select cm_beginamount,am_doublebegindebit-am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit-am_doubleendcredit,'应收' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowdebit-am_umdoublenowcredit,am_umdoubleenddebit-am_umdoubleendcredit from (select NVL2(cm_currency,cm_currency,cmc_currency) currency,NVL2(cm_yearmonth,cm_yearmonth,cmc_yearmonth) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(cm_beginamount, 0)-NVL(cm_gsbeginamounts, 0),2)) cm_beginamount,sum(round(NVL(cm_nowamount,0)-NVL(cm_gsnowamounts,0)+NVL(cm_gsinvoamounts,0),2)) cm_nowamount,sum(round(NVL(cm_payamount,0),2)) cm_payamount,sum(round( NVL(cm_endamount, 0)-NVL(cm_gsendamounts, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on cm_yearmonth=cmc_yearmonth and cm_currency=cmc_currency) where yearmonth=? order by currency";

	final static String AR_PRE_SUM = "SELECT sum(round(cm_prepaybegin,2)), sum(round(nvl(am_doublebegincredit, 0),2) - round(nvl(am_doublebegindebit, 0),2)), sum(round(cm_prepaybalance,2)), sum(round(am_doublenowdebit,2)), sum(round(cm_prepaynow,2)), sum(round(am_doublenowcredit,2)), sum(round(cm_prepayend,2)), sum(round(nvl(am_doubleendcredit, 0),2) - round(nvl(am_doubleenddebit, 0),2)), '预收' am_catecode, currency am_currency, sum(round(am_umdoublenowdebit,2)), sum(round(am_umdoublenowcredit,2)), sum(round(nvl(am_umdoublenowdebit, 0),2) - round(nvl(am_umdoublenowcredit, 0),2)), sum(round(nvl(am_umdoubleendcredit, 0),2) - round(nvl(am_umdoubleenddebit, 0),2)) am_umdoubleendamount FROM (SELECT (CASE WHEN NVL(cm_currency,' ')=' ' THEN am_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth, NVL(cm_prepaybegin,0) cm_prepaybegin, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(cm_prepaynow,0) cm_prepaynow, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(cm_prepaybalance,0) cm_prepaybalance, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(cm_prepayend,0) cm_prepayend, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM  CustMonth full join (SELECT sum(am_doublebegindebit) am_doublebegindebit ,sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit, sum(am_doubleenddebit) am_doubleenddebit, sum(am_doubleendcredit) am_doubleendcredit, sum(am_umdoubleenddebit) am_umdoubleenddebit, sum(am_umdoubleendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth,am_currency FROM AssMonth WHERE am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode,am_currency) on cm_yearmonth = am_yearmonth AND cm_custcode = am_asscode and cm_currency = am_currency) WHERE yearmonth = ? group by currency order by currency";
	final static String AR_PRE_SUM1 = "select cm_beginamount,am_doublebegindebit-am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit-am_doubleendcredit,'预收' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowdebit-am_umdoublenowcredit,am_umdoubleenddebit-am_umdoubleendcredit from (select (CASE WHEN NVL(cm_currency,' ')=' ' THEN cmc_currency ELSE cm_currency END) currency, (CASE WHEN NVL(cm_yearmonth,0)=0 THEN cmc_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(cm_prepaybegin, 0),2)) cm_beginamount,sum(round( NVL(cm_prepaybalance, 0),2)) cm_nowamount,sum(round(NVL(cm_prepaynow, 0),2)) cm_payamount,sum(round(NVL(cm_prepayend, 0),2)) cm_endamount,cm_yearmonth,cm_currency from custmonth group by cm_yearmonth,cm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency)  on cm_yearmonth = cmc_yearmonth and cm_currency = cmc_currency) where yearmonth = ? order by currency";

	final static String AR_GS_SUM = "select sum(round(cm_gsbeginamount,2)), sum(round(nvl(am_doublebegindebit, 0),2) - round(nvl(am_doublebegincredit, 0),2)), sum(round(cm_gsnowamount,2)), sum(round(am_doublenowdebit,2)), sum(round(cm_gsinvoamount,2)), sum(round(am_doublenowcredit,2)), sum(round(cm_gsendamount,2)), sum(round(nvl(am_doubleenddebit, 0) - nvl(am_doubleendcredit, 0),2)), '发出商品' am_catecode, 'RMB' am_currency, sum(am_umdoublenowdebit), sum(am_umdoublenowcredit), sum(round(nvl(am_umdoublenowdebit, 0) - nvl(am_umdoublenowcredit, 0),2)), sum(round(nvl(am_umdoubleenddebit, 0) - nvl(am_umdoubleendcredit, 0),2)) am_umdoubleendamount from (SELECT  (CASE WHEN NVL(cm_yearmonth, 0) = 0 THEN am_yearmonth  ELSE cm_yearmonth END) yearmonth, NVL(cm_gsbeginamount, 0) cm_gsbeginamount, NVL(am_doublebegindebit, 0) am_doublebegindebit, NVL(am_doublebegincredit, 0) am_doublebegincredit, NVL(cm_gsnowamount, 0) cm_gsnowamount, NVL(am_doublenowdebit, 0) am_doublenowdebit, NVL(cm_gsinvoamount, 0) cm_gsinvoamount, NVL(am_doublenowcredit, 0) am_doublenowcredit, NVL(cm_gsendamount, 0) cm_gsendamount, NVL(am_doubleenddebit, 0) am_doubleenddebit, NVL(am_doubleendcredit, 0) am_doubleendcredit, NVL(am_umdoublenowdebit, 0) am_umdoublenowdebit, NVL(am_umdoublenowcredit, 0) am_umdoublenowcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM (SELECT sum(am_begindebit) am_doublebegindebit ,sum(am_begincredit) am_doublebegincredit, sum(am_umnowdebit) am_umdoublenowdebit, sum(am_umnowcredit) am_umdoublenowcredit, sum(am_nowdebit) am_doublenowdebit, sum(am_nowcredit) am_doublenowcredit, sum(am_enddebit) am_doubleenddebit, sum(am_endcredit) am_doubleendcredit, sum(am_umenddebit) am_umdoubleenddebit, sum(am_umendcredit) am_umdoubleendcredit,am_asscode,am_yearmonth FROM assmonth WHERE am_catecode in (@CODE) and am_asstype='客户往来' group by am_yearmonth,am_asscode) full join custmonth on cm_yearmonth = am_yearmonth and cm_custcode = am_asscode AND cm_currency=getconfig('sys','defaultCurrency')) where yearmonth = ?";
	final static String AR_GS_SUM1 = "select cm_beginamount,am_doublebegindebit-am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit-am_doubleendcredit,'发出商品' am_catecode,'RMB' am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowdebit-am_umdoublenowcredit,am_umdoubleenddebit-am_umdoubleendcredit from (select (CASE WHEN NVL(cm_yearmonth,0)=0 THEN am_yearmonth ELSE cm_yearmonth END) yearmonth,cm_beginamount,am_doublebegindebit,am_doublebegincredit,cm_nowamount,am_doublenowdebit,cm_payamount,am_doublenowcredit,cm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round( NVL(cm_gsbeginamount, 0),2)) cm_beginamount,sum(round( NVL(cm_gsnowamount, 0),2)) cm_nowamount,sum(round( NVL(cm_gsinvoamount, 0),2)) cm_payamount,sum(round( NVL(cm_gsendamount, 0),2)) cm_endamount,cm_yearmonth from custmonth group by cm_yearmonth)  full join (select cm_yearmonth am_yearmonth,sum(round( NVL(cm_begindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cm_begincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cm_nowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cm_umnowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cm_nowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cm_umnowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cm_enddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cm_umenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cm_endcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cm_umendcredit, 0),2)) am_umdoubleendcredit from (select cm_yearmonth,cm_begindebit,cm_begincredit,cm_nowdebit,cm_umnowdebit,cm_nowcredit,cm_umnowcredit,cm_enddebit,cm_umenddebit,cm_endcredit,cm_umendcredit from catemonth where cm_catecode in (@CODE)) group by cm_yearmonth) on cm_yearmonth = am_yearmonth) where yearmonth = ?";

	final static String AP = "SELECT '应付' am_catecode, vendcode am_asscode, max(ve_name) am_assname, currency am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_beginamount,2)) vm_beginamount, 0 vm_umnowamount, 0 vm_umendamount, sum(round(vm_nowamount,2)) vm_nowamount, sum(round(vm_payamount,2)) vm_payamount, sum(round(vm_endamount,2)) vm_endamount, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit, sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit FROM (SELECT  (CASE WHEN NVL(vm_vendcode,' ')=' ' THEN am_asscode ELSE vm_vendcode END) vendcode, (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(vm_beginamount,0) vm_beginamount, NVL(vm_nowamount,0) vm_nowamount, NVL(vm_payamount,0) vm_payamount, NVL(vm_endamount,0) vm_endamount, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM VendMonth full join (select sum(am_doublebegindebit) am_doublebegindebit, sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit,am_yearmonth,am_asscode,am_currency from AssMonth where am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth AND vm_vendcode = am_asscode and vm_currency = am_currency ) LEFT JOIN VENDOR ON ve_code = vendcode WHERE yearmonth = ? group by vendcode, currency  order by vendcode ";
	final static String AP1 = "select '应付' AM_CATECODE,VENDCODE AM_ASSCODE,VE_NAME AM_ASSNAME,CURRENCY AM_CURRENCY,SUM(ROUND(AM_DOUBLEBEGINDEBIT,2)) AM_DOUBLEBEGINDEBIT,sum(round(am_doublebegincredit,2)) am_doublebegincredit,sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit,sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_beginamount-vm_esbeginamounts,2)) vm_beginamount, 0 vm_umnowamount, 0 vm_umendamount, sum(round(vm_nowamount-vm_esnowamounts+vm_esinvoamounts,2)) vm_nowamount, sum(round(vm_payamount,2)) vm_payamount, sum(round(vm_endamount-vm_esendamounts,2)) vm_endamount, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit, sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit FROM (SELECT NVL2(vm_vendcode,vm_vendcode,am_asscode) vendcode,NVL2(vm_currency,vm_currency,am_currency) currency,NVL2(vm_yearmonth,vm_yearmonth,am_yearmonth) yearmonth, NVL(am_doublebegindebit,0) am_doublebegindebit,NVL(am_doublebegincredit,0) am_doublebegincredit,NVL(am_umdoublenowdebit,0) am_umdoublenowdebit,NVL(am_umdoublenowcredit,0) am_umdoublenowcredit,NVL(am_doublenowdebit,0) am_doublenowdebit,NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(vm_beginamount,0) vm_beginamount, NVL(vm_nowamount,0) vm_nowamount, NVL(vm_payamount,0) vm_payamount, NVL(vm_endamount,0) vm_endamount,nvl(vm_esbeginamounts,0) vm_esbeginamounts, nvl(vm_esnowamounts,0) vm_esnowamounts, nvl(vm_esinvoamounts,0) vm_esinvoamounts, nvl(vm_esendamounts,0) vm_esendamounts, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM VendMonth full join (select sum(nvl(am_doublebegindebit,0)) am_doublebegindebit, sum(nvl(am_doublebegincredit,0)) am_doublebegincredit, sum(nvl(am_umdoublenowdebit,0)) am_umdoublenowdebit,sum(nvl(am_umdoublenowcredit,0)) am_umdoublenowcredit,sum(nvl(am_doublenowdebit,0)) am_doublenowdebit,sum(nvl(am_doublenowcredit,0)) am_doublenowcredit,sum(nvl(am_doubleenddebit,0)) am_doubleenddebit,sum(nvl(am_doubleendcredit,0)) am_doubleendcredit,sum(nvl(am_umdoubleenddebit,0)) am_umdoubleenddebit,sum(nvl(am_umdoubleendcredit,0)) am_umdoubleendcredit,am_yearmonth,am_asscode,am_currency from AssMonth where am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth=am_yearmonth AND vm_vendcode=am_asscode and vm_currency=am_currency) LEFT JOIN VENDOR ON ve_code=vendcode WHERE yearmonth=? group by vendcode,ve_name,currency  order by vendcode";
	final static String AP2 = "select '应付' am_catecode, '' am_asscode, '' am_assname, currency am_currency,vm_beginamount,vm_payamount,vm_nowamount,vm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round( NVL(vm_beginamount, 0),2)) vm_beginamount,sum(round( NVL(vm_payamount, 0),2)) vm_payamount,sum(round( NVL(vm_nowamount, 0),2)) vm_nowamount,sum(round( NVL(vm_endamount, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on vm_yearmonth = cmc_yearmonth and vm_currency = cmc_currency) where yearmonth = ? order by currency";
	final static String AP3 = "select '应付' am_catecode,'' am_asscode,'' am_assname,currency am_currency,vm_beginamount,vm_payamount,vm_nowamount,vm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select NVL2(vm_currency,vm_currency,cmc_currency) currency,NVL2(vm_yearmonth,vm_yearmonth,cmc_yearmonth) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(vm_beginamount,0)-NVL(vm_esbeginamounts,0),2)) vm_beginamount,sum(round( NVL(vm_payamount,0)-NVL(vm_esinvoamounts,0)+NVL(vm_esnowamounts,0),2)) vm_payamount,sum(round(NVL(vm_nowamount,0),2)) vm_nowamount,sum(round( NVL(vm_endamount,0)-NVL(vm_esendamounts,0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on vm_yearmonth=cmc_yearmonth and vm_currency=cmc_currency) where yearmonth=? order by currency";

	final static String AP_PRE = "SELECT '预付' am_catecode, vendcode am_asscode, max(ve_name) am_assname, currency am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_prepaybegin,2)) vm_beginamount, 0 vm_umnowamount, 0 vm_umendamount, sum(round(vm_prepaynow,2)) vm_payamount, sum(round(vm_prepaybalance,2)) vm_nowamount, sum(round(vm_prepayend,2)) vm_endamount, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit, sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit FROM (SELECT (CASE WHEN NVL(vm_vendcode,' ')=' ' THEN am_asscode ELSE vm_vendcode END) vendcode, (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(vm_prepaybegin,0) vm_prepaybegin, NVL(vm_prepaynow,0) vm_prepaynow, NVL(vm_prepaybalance,0) vm_prepaybalance, NVL(vm_prepayend,0) vm_prepayend, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM VendMonth full join (select am_yearmonth,am_asscode,am_currency,sum(am_doublebegindebit) am_doublebegindebit,sum(am_doublebegincredit) am_doublebegincredit,sum(am_doublenowdebit) am_doublenowdebit,sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoublenowdebit) am_umdoublenowdebit,sum(am_umdoublenowcredit) am_umdoublenowcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit from AssMonth where am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth AND vm_vendcode = am_asscode and vm_currency = am_currency ) LEFT JOIN VENDOR ON ve_code=vendcode WHERE yearmonth = ? group by vendcode, currency order by vendcode ";
	final static String AP_PRE1 = "select '预付' am_catecode, '' am_asscode, '' am_assname,  currency am_currency,vm_beginamount,vm_payamount,vm_nowamount,vm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round( NVL(vm_prepaybegin, 0),2)) vm_beginamount,sum(round( NVL(vm_prepaynow, 0),2)) vm_payamount,sum(round( NVL(vm_prepaybalance, 0),2)) vm_nowamount,sum(round( NVL(vm_prepayend, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency)  on  vm_yearmonth = cmc_yearmonth and  vm_currency =  cmc_currency)  where yearmonth = ? order by currency";

	final static String AP_ES = "select '应付暂估' am_catecode, vendcode am_asscode, max(ve_name) am_assname, currency am_currency, sum(round(am_doublebegindebit,2)) am_doublebegindebit, sum(round(am_doublebegincredit,2)) am_doublebegincredit, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(am_doubleenddebit,2)) am_doubleenddebit, sum(round(am_doubleendcredit,2)) am_doubleendcredit, sum(round(vm_esbeginamount,2)) vm_beginamount, 0 vm_umnowamount, 0 vm_umendamount, sum(round(vm_esnowamount,2)) vm_nowamount, sum(round(vm_esinvoamount,2)) vm_payamount, sum(round(vm_esendamount,2)) vm_endamount, sum(round(am_umdoubleenddebit,2)) am_umdoubleenddebit, sum(round(am_umdoubleendcredit,2)) am_umdoubleendcredit from ( SELECT  (CASE WHEN NVL(vm_vendcode,' ')=' ' THEN am_asscode ELSE vm_vendcode END) vendcode, (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_doublenowdebit,0) am_doublenowdebit,  NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(vm_esbeginamount,0) vm_esbeginamount, NVL(vm_esnowamount,0) vm_esnowamount, NVL(vm_esinvoamount,0) vm_esinvoamount, NVL(vm_esendamount,0) vm_esendamount, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM vendmonth full join (SELECT am_yearmonth,am_asscode,am_currency,sum(am_doublebegindebit) am_doublebegindebit,sum(am_doublebegincredit) am_doublebegincredit,sum(am_doublenowdebit) am_doublenowdebit,sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoublenowdebit) am_umdoublenowdebit,sum(am_umdoublenowcredit) am_umdoublenowcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit FROM assmonth WHERE am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth and vm_vendcode = am_asscode and vm_currency = am_currency ) LEFT JOIN VENDOR ON ve_code=vendcode where yearmonth = ? group by vendcode, currency order by vendcode";
	final static String AP_ES1 = "select '应付暂估' am_catecode, '' am_asscode, '' am_assname,currency am_currency,vm_beginamount,vm_payamount,vm_nowamount,vm_endamount,am_doublebegindebit,am_doublebegincredit,am_doublenowdebit,am_umdoublenowdebit,am_doublenowcredit,am_umdoublenowcredit,am_doubleenddebit,am_umdoubleenddebit,am_doubleendcredit,am_umdoubleendcredit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency,(CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round(NVL(vm_esbeginamount, 0),2)) vm_beginamount,sum(round(NVL(vm_esnowamount, 0),2)) vm_payamount,sum(round(NVL(vm_esinvoamount, 0),2)) vm_nowamount,sum(round(NVL(vm_esendamount, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency)  full join (select cmc_yearmonth,cmc_currency,sum(round(NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round(NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round(NVL(cmc_nowdebit, 0),2)) am_doublenowdebit,sum(round(NVL(cmc_umnowdebit, 0),2)) am_umdoublenowdebit,sum(round(NVL(cmc_nowcredit, 0),2)) am_doublenowcredit,sum(round(NVL(cmc_umnowcredit, 0),2)) am_umdoublenowcredit,sum(round(NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round(NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round(NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round(NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_currency,cmc_yearmonth,cmc_doublebegindebit,cmc_doublebegincredit,cmc_nowdebit,cmc_umnowdebit,cmc_nowcredit,cmc_umnowcredit,cmc_doubleenddebit,cmc_umdoubleenddebit,cmc_doubleendcredit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on vm_yearmonth = cmc_yearmonth and vm_currency = cmc_currency) where yearmonth = ? order by currency";

	final static String AP_SUM = "select sum(round(vm_beginamount,2)) vm_beginamount, sum(round(nvl(am_doublebegincredit, 0),2) - round(nvl(am_doublebegindebit, 0),2)) am_doublebeginamount, sum(round(vm_nowamount,2)) vm_nowamount, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(vm_payamount,2)) vm_payamount, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_endamount,2)) vm_endamount, sum(round(nvl(am_doubleendcredit, 0),2) - round(nvl(am_doubleenddebit, 0),2)) am_doubleendamount, '应付' am_catecode, currency am_currency, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(nvl(am_umdoubleendcredit, 0),2) - round(nvl(am_umdoubleenddebit, 0),2)) am_umdoubleendamount from (SELECT  (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(vm_beginamount,0) vm_beginamount, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(vm_nowamount,0) vm_nowamount, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(vm_payamount,0) vm_payamount, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(vm_endamount,0) vm_endamount, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit FROM vendmonth full join (SELECT sum(am_doublebegindebit) am_doublebegindebit, sum(am_doublebegincredit) am_doublebegincredit, sum(am_umdoublenowdebit) am_umdoublenowdebit, sum(am_umdoublenowcredit) am_umdoublenowcredit, sum(am_doublenowdebit) am_doublenowdebit, sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit,am_yearmonth,am_asscode,am_currency FROM assmonth WHERE am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth and vm_vendcode = am_asscode and vm_currency = am_currency) where yearmonth = ? group by currency order by currency";
	final static String AP_SUM1 = "select SUM(ROUND(VM_BEGINAMOUNT-VM_ESBEGINAMOUNTS,2)) VM_BEGINAMOUNT, SUM(ROUND(NVL(AM_DOUBLEBEGINCREDIT,0),2)-ROUND(NVL(AM_DOUBLEBEGINDEBIT,0),2)) AM_DOUBLEBEGINAMOUNT, SUM(ROUND(VM_NOWAMOUNT-VM_ESNOWAMOUNTS+VM_ESINVOAMOUNTS,2)) VM_NOWAMOUNT, SUM(ROUND(AM_DOUBLENOWDEBIT,2)) AM_DOUBLENOWDEBIT, SUM(ROUND(VM_PAYAMOUNT,2)) VM_PAYAMOUNT, SUM(ROUND(AM_DOUBLENOWCREDIT,2)) AM_DOUBLENOWCREDIT, SUM(ROUND(VM_ENDAMOUNT-VM_ESENDAMOUNTS,2)) VM_ENDAMOUNT, SUM(ROUND(NVL(AM_DOUBLEENDCREDIT, 0),2) - ROUND(NVL(AM_DOUBLEENDDEBIT, 0),2)) AM_DOUBLEENDAMOUNT, '应付' AM_CATECODE, CURRENCY AM_CURRENCY, SUM(ROUND(AM_UMDOUBLENOWDEBIT,2)) AM_UMDOUBLENOWDEBIT, SUM(ROUND(AM_UMDOUBLENOWCREDIT,2)) AM_UMDOUBLENOWCREDIT, SUM(ROUND(NVL(AM_UMDOUBLEENDCREDIT, 0),2) - ROUND(NVL(AM_UMDOUBLEENDDEBIT, 0),2)) AM_UMDOUBLEENDAMOUNT from (select NVL2(VM_CURRENCY,VM_CURRENCY,AM_CURRENCY) CURRENCY,NVL2(VM_YEARMONTH,VM_YEARMONTH,AM_YEARMONTH) YEARMONTH,NVL(VM_BEGINAMOUNT,0) VM_BEGINAMOUNT,NVL(AM_DOUBLEBEGINCREDIT,0) AM_DOUBLEBEGINCREDIT, NVL(AM_DOUBLEBEGINDEBIT,0) AM_DOUBLEBEGINDEBIT, NVL(VM_NOWAMOUNT,0) VM_NOWAMOUNT, NVL(AM_DOUBLENOWDEBIT,0) AM_DOUBLENOWDEBIT, NVL(VM_PAYAMOUNT,0) VM_PAYAMOUNT, NVL(AM_DOUBLENOWCREDIT,0) AM_DOUBLENOWCREDIT, NVL(VM_ENDAMOUNT,0) VM_ENDAMOUNT,NVL(VM_ESBEGINAMOUNTS,0) VM_ESBEGINAMOUNTS, NVL(VM_ESNOWAMOUNTS,0) VM_ESNOWAMOUNTS, NVL(VM_ESINVOAMOUNTS,0) VM_ESINVOAMOUNTS, NVL(VM_ESENDAMOUNTS,0) VM_ESENDAMOUNTS, NVL(AM_DOUBLEENDCREDIT,0) AM_DOUBLEENDCREDIT, NVL(AM_DOUBLEENDDEBIT,0) AM_DOUBLEENDDEBIT, NVL(AM_UMDOUBLENOWDEBIT,0) AM_UMDOUBLENOWDEBIT, NVL(AM_UMDOUBLENOWCREDIT,0) AM_UMDOUBLENOWCREDIT, NVL(AM_UMDOUBLEENDCREDIT,0) AM_UMDOUBLEENDCREDIT, NVL(AM_UMDOUBLEENDDEBIT,0) AM_UMDOUBLEENDDEBIT from VENDMONTH full join (select SUM(AM_DOUBLEBEGINDEBIT) AM_DOUBLEBEGINDEBIT, SUM(AM_DOUBLEBEGINCREDIT) AM_DOUBLEBEGINCREDIT, SUM(AM_UMDOUBLENOWDEBIT) AM_UMDOUBLENOWDEBIT, SUM(AM_UMDOUBLENOWCREDIT) AM_UMDOUBLENOWCREDIT, SUM(AM_DOUBLENOWDEBIT) AM_DOUBLENOWDEBIT, SUM(AM_DOUBLENOWCREDIT) AM_DOUBLENOWCREDIT,SUM(AM_DOUBLEENDDEBIT) AM_DOUBLEENDDEBIT,SUM(AM_DOUBLEENDCREDIT) AM_DOUBLEENDCREDIT,SUM(AM_UMDOUBLEENDDEBIT) AM_UMDOUBLEENDDEBIT,SUM(AM_UMDOUBLEENDCREDIT) AM_UMDOUBLEENDCREDIT,AM_YEARMONTH,AM_ASSCODE,AM_CURRENCY from ASSMONTH where AM_CATECODE in (@CODE) and AM_ASSTYPE='供应商往来' group by AM_YEARMONTH,AM_ASSCODE,AM_CURRENCY) on VM_YEARMONTH = AM_YEARMONTH and VM_VENDCODE=AM_ASSCODE and VM_CURRENCY=AM_CURRENCY) where YEARMONTH=? group by CURRENCY order by CURRENCY";
	final static String AP_SUM2 = "select vm_beginamount,am_doublebegincredit-am_doublebegindebit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleendcredit-am_doubleenddebit,'应付' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowcredit-am_umdoublenowdebit,am_umdoubleendcredit-am_umdoubleenddebit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round(NVL(vm_beginamount, 0),2)) vm_beginamount,sum(round( NVL(vm_payamount, 0),2)) vm_payamount,sum(round(NVL(vm_nowamount, 0),2)) vm_nowamount,sum(round(NVL(vm_endamount, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round(NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round(NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round(NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round(NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round(NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency)  on vm_yearmonth = cmc_yearmonth and vm_currency = cmc_currency) where yearmonth = ? order by currency";
	final static String AP_SUM3 = "select vm_beginamount,am_doublebegincredit-am_doublebegindebit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleendcredit-am_doubleenddebit,'应付' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowcredit-am_umdoublenowdebit,am_umdoubleendcredit-am_umdoubleenddebit from (select NVL2(vm_currency,vm_currency,cmc_currency) currency, NVL2(vm_yearmonth,vm_yearmonth,cmc_yearmonth) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(vm_beginamount, 0)-NVL(vm_esbeginamounts, 0),2)) vm_beginamount,sum(round(NVL(vm_payamount,0)-NVL(vm_esinvoamounts,0)+NVL(vm_esnowamounts,0),2)) vm_payamount,sum(round(NVL(vm_nowamount,0),2)) vm_nowamount,sum(round( NVL(vm_endamount,0)-NVL(vm_esendamounts,0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on vm_yearmonth=cmc_yearmonth and vm_currency=cmc_currency) where yearmonth=? order by currency";

	final static String AP_PRE_SUM = "select sum(round(vm_prepaybegin,2)) vm_beginamount, sum(round(nvl(am_doublebegindebit, 0),2) - round(nvl(am_doublebegincredit, 0),2)) am_doublebeginamount, sum(round(vm_prepaybalance,2)) vm_nowamount, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(vm_prepaynow,2)) vm_payamount, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_prepayend,2)) vm_endamount, sum(round(nvl(am_doubleenddebit, 0),2) - round(nvl(am_doubleendcredit, 0),2)) am_doubleendamount, '预付' am_catecode, currency am_currency, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(nvl(am_umdoubleenddebit, 0),2) - round(nvl(am_umdoubleendcredit, 0),2)) am_umdoubleendamount from ( SELECT (CASE WHEN NVL(vm_currency,' ')=' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN am_yearmonth ELSE vm_yearmonth END) yearmonth, NVL(vm_prepaybegin,0) vm_prepaybegin, NVL(am_doublebegindebit,0) am_doublebegindebit, NVL(am_doublebegincredit,0) am_doublebegincredit, NVL(vm_prepaynow,0) vm_prepaynow, NVL(am_doublenowdebit,0) am_doublenowdebit, NVL(vm_prepaybalance,0) vm_prepaybalance, NVL(am_doublenowcredit,0) am_doublenowcredit, NVL(vm_prepayend,0) vm_prepayend, NVL(am_doubleenddebit,0) am_doubleenddebit, NVL(am_doubleendcredit,0) am_doubleendcredit, NVL(am_umdoublenowdebit,0) am_umdoublenowdebit, NVL(am_umdoublenowcredit,0) am_umdoublenowcredit, NVL(am_umdoubleenddebit,0) am_umdoubleenddebit, NVL(am_umdoubleendcredit,0) am_umdoubleendcredit FROM  vendmonth full join (SELECT am_yearmonth,am_asscode,am_currency,sum(am_doublebegindebit) am_doublebegindebit,sum(am_doublebegincredit) am_doublebegincredit,sum(am_doublenowdebit) am_doublenowdebit,sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoublenowdebit) am_umdoublenowdebit,sum(am_umdoublenowcredit) am_umdoublenowcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit FROM assmonth WHERE am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth and vm_vendcode = am_asscode and vm_currency = am_currency) where yearmonth = ? group by currency order by currency";
	final static String AP_PRE_SUM1 = "select vm_beginamount,am_doublebegindebit-am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit-am_doubleendcredit,'预付' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowdebit-am_umdoublenowcredit,am_umdoubleenddebit-am_umdoubleendcredit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit from (select sum(round(NVL(vm_prepaybegin, 0),2)) vm_beginamount,sum(round( NVL(vm_prepaynow, 0),2)) vm_payamount,sum(round(NVL(vm_prepaybalance, 0),2)) vm_nowamount,sum(round(NVL(vm_prepayend, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency) full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round( NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round( NVL(cmc_doublenowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umdoublenowdebit, 0),2)) am_umdoublenowdebit,sum(round( NVL(cmc_doublenowcredit, 0),2)) am_doublenowcredit,sum(round( NVL(cmc_umdoublenowcredit, 0),2)) am_umdoublenowcredit,sum(round( NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round( NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round( NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_yearmonth,cmc_currency,cmc_doublebegindebit,cmc_doublebegincredit,cmc_doublenowdebit,cmc_doublenowcredit,cmc_doubleenddebit,cmc_doubleendcredit,cmc_umdoublenowdebit,cmc_umdoublenowcredit,cmc_umdoubleenddebit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency)  on vm_yearmonth = cmc_yearmonth and vm_currency = cmc_currency) where yearmonth = ? order by currency";

	final static String AP_ES_SUM = "select sum(round(vm_esbeginamount,2)) vm_beginamount, sum(round(nvl(am_doublebegincredit, 0),2) - round(nvl(am_doublebegindebit, 0),2)) am_doublebeginamount, sum(round(vm_esnowamount,2)) vm_nowamount, sum(round(am_doublenowdebit,2)) am_doublenowdebit, sum(round(vm_esinvoamount,2)) vm_payamount, sum(round(am_doublenowcredit,2)) am_doublenowcredit, sum(round(vm_esendamount,2)) vm_endamount, sum(round(nvl(am_doubleendcredit, 0),2) - round(nvl(am_doubleenddebit, 0),2)) am_doubleendamount, '应付暂估' am_catecode, currency am_currency, sum(round(am_umdoublenowdebit,2)) am_umdoublenowdebit, sum(round(am_umdoublenowcredit,2)) am_umdoublenowcredit, sum(round(nvl(am_umdoubleendcredit, 0),2) - round(nvl(am_umdoubleenddebit, 0),2)) am_umdoubleendamount from (SELECT (CASE WHEN NVL(vm_currency, ' ') = ' ' THEN am_currency ELSE vm_currency END) currency, (CASE WHEN NVL(vm_yearmonth, 0) = 0 THEN am_yearmonth  ELSE vm_yearmonth END) yearmonth, NVL(vm_esbeginamount, 0) vm_esbeginamount, NVL(am_doublebegincredit, 0) am_doublebegincredit, NVL(am_doublebegindebit, 0) am_doublebegindebit,  NVL(vm_esnowamount, 0) vm_esnowamount, NVL(am_doublenowdebit, 0) am_doublenowdebit, NVL(vm_esinvoamount, 0) vm_esinvoamount, NVL(am_doublenowcredit, 0) am_doublenowcredit, NVL(vm_esendamount, 0) vm_esendamount, NVL(am_doubleendcredit, 0) am_doubleendcredit, NVL(am_doubleenddebit, 0) am_doubleenddebit, NVL(am_umdoublenowdebit, 0) am_umdoublenowdebit, NVL(am_umdoublenowcredit, 0) am_umdoublenowcredit, NVL(am_umdoubleendcredit, 0) am_umdoubleendcredit, NVL(am_umdoubleenddebit, 0) am_umdoubleenddebit FROM vendmonth full join (SELECT am_yearmonth,am_asscode,am_currency,sum(am_doublebegindebit) am_doublebegindebit,sum(am_doublebegincredit) am_doublebegincredit,sum(am_doublenowdebit) am_doublenowdebit,sum(am_doublenowcredit) am_doublenowcredit,sum(am_doubleenddebit) am_doubleenddebit,sum(am_doubleendcredit) am_doubleendcredit,sum(am_umdoublenowdebit) am_umdoublenowdebit,sum(am_umdoublenowcredit) am_umdoublenowcredit,sum(am_umdoubleenddebit) am_umdoubleenddebit,sum(am_umdoubleendcredit) am_umdoubleendcredit FROM assmonth WHERE am_catecode in (@CODE) and am_asstype='供应商往来' group by am_yearmonth,am_asscode,am_currency) on vm_yearmonth = am_yearmonth and vm_vendcode = am_asscode and vm_currency = am_currency) where yearmonth = ? group by currency order by currency";
	final static String AP_ES_SUM1 = "select vm_beginamount,am_doublebegincredit-am_doublebegindebit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleendcredit-am_doubleenddebit,'应付暂估' am_catecode,currency am_currency,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoublenowcredit-am_umdoublenowdebit,am_umdoubleendcredit-am_umdoubleenddebit from (select (CASE WHEN NVL(vm_currency,' ')=' ' THEN cmc_currency ELSE vm_currency END) currency,(CASE WHEN NVL(vm_yearmonth,0)=0 THEN cmc_yearmonth ELSE vm_yearmonth END) yearmonth,vm_beginamount,am_doublebegindebit,am_doublebegincredit,vm_payamount,am_doublenowdebit,vm_nowamount,am_doublenowcredit,vm_endamount,am_doubleenddebit,am_doubleendcredit,am_umdoublenowdebit,am_umdoublenowcredit,am_umdoubleenddebit,am_umdoubleendcredit  from (select sum(round(NVL(vm_esbeginamount, 0),2)) vm_beginamount,sum(round( NVL(vm_esnowamount, 0),2)) vm_payamount,sum(round(NVL(vm_esinvoamount, 0),2)) vm_nowamount,sum(round(NVL(vm_esendamount, 0),2)) vm_endamount,vm_yearmonth,vm_currency from vendmonth group by vm_yearmonth,vm_currency)  full join (select cmc_yearmonth,cmc_currency,sum(round( NVL(cmc_doublebegindebit, 0),2)) am_doublebegindebit,sum(round(NVL(cmc_doublebegincredit, 0),2)) am_doublebegincredit,sum(round(NVL(cmc_nowdebit, 0),2)) am_doublenowdebit,sum(round( NVL(cmc_umnowdebit, 0),2)) am_umdoublenowdebit,sum(round(NVL(cmc_nowcredit, 0),2)) am_doublenowcredit,sum(round(NVL(cmc_umnowcredit, 0),2)) am_umdoublenowcredit,sum(round(NVL(cmc_doubleenddebit, 0),2)) am_doubleenddebit,sum(round(NVL(cmc_umdoubleenddebit, 0),2)) am_umdoubleenddebit,sum(round(NVL(cmc_doubleendcredit, 0),2)) am_doubleendcredit,sum(round( NVL(cmc_umdoubleendcredit, 0),2)) am_umdoubleendcredit from (select cmc_currency,cmc_yearmonth,cmc_doublebegindebit,cmc_doublebegincredit,cmc_nowdebit,cmc_umnowdebit,cmc_nowcredit,cmc_umnowcredit,cmc_doubleenddebit,cmc_umdoubleenddebit,cmc_doubleendcredit,cmc_umdoubleendcredit from catemonthcurrency where cmc_catecode in (@CODE)) group by cmc_yearmonth,cmc_currency) on vm_yearmonth = cmc_yearmonth and vm_currency = cmc_currency) where yearmonth = ? order by currency";

	public List<Map<String, Object>> getArAccount(String condition) {
		JSONObject js = JSONObject.fromObject(condition);
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-C");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		// 包括未记账凭证
		boolean chkun = js.getBoolean("chkun");
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 应收账款科目
		String[] arCates = baseDao.getDBSettingArray("MonthAccount", "arCatecode");
		if (arCates == null || arCates.length == 0) {
			BaseUtil.showError("未设置应收科目");
		}
		String[] prCates = baseDao.getDBSettingArray("MonthAccount", "preRecCatecode");
		String[] gsCates = baseDao.getDBSettingArray("MonthAccount", "gsCatecode");
		// 执行预登账操作
		if (chkun) {
			String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { ym, ym, null, null });
			if (res != null && res.trim().length() > 0) {
				BaseUtil.showError(res);
			}
		}
		// 刷新数据
		String res = baseDao.callProcedure("SP_REFRESHCUSTMONTH", new Object[] { ym });
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
		if (baseDao.isDBSetting("useBillOutAR")) {
			if (hasCustAsskind(arCates)) {
				store.addAll(getArData(AR1, ym, CollectionUtil.toSqlString(arCates), chkun, 1));
			} else {
				store.addAll(getArData(AR3, ym, CollectionUtil.toSqlString(arCates), chkun, 1));
			}

			if (prCates != null && prCates.length > 0) {
				if (hasCustAsskind(prCates)) {
					store.addAll(getArData(AR_PRE, ym, CollectionUtil.toSqlString(prCates), chkun, -1));// 预收的用
																										// 贷-借
				} else {
					store.addAll(getArData(AR_PRE1, ym, CollectionUtil.toSqlString(prCates), chkun, -1));// 预收的用
				} // 贷-借
			}

			if (gsCates != null && gsCates.length > 0) {
				if (hasCustAsskind(gsCates)) {
					store.addAll(getArData(AR_GS, ym, CollectionUtil.toSqlString(gsCates), chkun, 1));
				} else {
					store.addAll(getArData(AR_GS1, ym, CollectionUtil.toSqlString(gsCates), chkun, 1));
				}
			}
			if (hasCustAsskind(arCates)) {
				store.addAll(getArCount(AR_SUM1, ym, CollectionUtil.toSqlString(arCates), chkun));// 合计
			} else {
				store.addAll(getArCount(AR_SUM3, ym, CollectionUtil.toSqlString(arCates), chkun));// 合计
			}
			if (prCates != null && prCates.length > 0) {
				if (hasCustAsskind(prCates)) {
					store.addAll(getArCount(AR_PRE_SUM, ym, CollectionUtil.toSqlString(prCates), chkun));
				} else {
					store.addAll(getArCount(AR_PRE_SUM1, ym, CollectionUtil.toSqlString(prCates), chkun));
				}
			}
			if (gsCates != null && gsCates.length > 0) {
				if (hasCustAsskind(gsCates)) {
					store.addAll(getArCount(AR_GS_SUM, ym, CollectionUtil.toSqlString(gsCates), chkun));
				} else {
					store.addAll(getArCount(AR_GS_SUM1, ym, CollectionUtil.toSqlString(gsCates), chkun));
				}
			}
		} else {
			if (hasCustAsskind(arCates)) {
				store.addAll(getArData(AR, ym, CollectionUtil.toSqlString(arCates), chkun, 1));
			} else {
				store.addAll(getArData(AR2, ym, CollectionUtil.toSqlString(arCates), chkun, 1));
			}
			if (prCates != null && prCates.length > 0)
				if (hasCustAsskind(prCates)) {
					store.addAll(getArData(AR_PRE, ym, CollectionUtil.toSqlString(prCates), chkun, -1));// 预收的用
																										// 贷-借
				} else {
					store.addAll(getArData(AR_PRE1, ym, CollectionUtil.toSqlString(prCates), chkun, -1));// 预收的用
				}
			if (gsCates != null && gsCates.length > 0) {
				if (hasCustAsskind(gsCates)) {
					store.addAll(getArData(AR_GS, ym, CollectionUtil.toSqlString(gsCates), chkun, 1));
				} else {
					store.addAll(getArData(AR_GS1, ym, CollectionUtil.toSqlString(gsCates), chkun, 1));
				}
			}
			if (hasCustAsskind(arCates)) {
				store.addAll(getArCount(AR_SUM, ym, CollectionUtil.toSqlString(arCates), chkun));// 合计
			} else {
				store.addAll(getArCount(AR_SUM2, ym, CollectionUtil.toSqlString(arCates), chkun));// 合计
			}
			if (prCates != null && prCates.length > 0)
				if (hasCustAsskind(prCates)) {
					store.addAll(getArCount(AR_PRE_SUM, ym, CollectionUtil.toSqlString(prCates), chkun));
				} else {
					store.addAll(getArCount(AR_PRE_SUM1, ym, CollectionUtil.toSqlString(prCates), chkun));
				}
			if (gsCates != null && gsCates.length > 0)
				if (hasCustAsskind(gsCates)) {
					store.addAll(getArCount(AR_GS_SUM, ym, CollectionUtil.toSqlString(gsCates), chkun));
				} else {
					store.addAll(getArCount(AR_GS_SUM1, ym, CollectionUtil.toSqlString(gsCates), chkun));
				}
		}
		return store;
	}

	private boolean hasCustAsskind(String[] codes) {
		int count = 0;
		for (String code : codes) {
			Object asstype = baseDao.getFieldDataByCondition("Category", "ca_asstype", "ca_code='" + code + "'");
			if (asstype == null) {
				return false;
			} else {
				String[] asstypes = asstype.toString().split("#");
				for (String type : asstypes) {
					if ("Cust".equals(type)) {
						count++;
						break;
					}
				}
			}
		}
		return count == codes.length;
	}

	private List<Map<String, Object>> getArData(String sql, int yearmonth, String cateCode, boolean chkun, int direct) {
		SqlRowList rs = baseDao.queryForRowSet(sql.replace("@CODE", cateCode), yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("am_yearmonth", yearmonth);
			item.put("am_catecode", rs.getString("am_catecode"));
			item.put("am_asscode", rs.getString("am_asscode"));
			item.put("am_assname", rs.getString("am_assname"));
			item.put("am_currency", rs.getString("am_currency"));
			item.put("cm_beginbalance", rs.getGeneralDouble("cm_beginamount"));
			item.put("am_beginbalance", direct * (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")));
			if (chkun) {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowamount"));
				item.put("am_nowdebit", rs.getGeneralDouble("am_umdoublenowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_payamount"));
				item.put("am_nowcredit", rs.getGeneralDouble("am_umdoublenowcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_endamount"));
				item.put("am_endbalance",
						direct * (rs.getGeneralDouble("am_umdoubleenddebit") - rs.getGeneralDouble("am_umdoubleendcredit")));
			} else {
				item.put("cm_nowdebit", rs.getGeneralDouble("cm_nowamount"));
				item.put("am_nowdebit", rs.getGeneralDouble("am_doublenowdebit"));
				item.put("cm_nowcredit", rs.getGeneralDouble("cm_payamount"));
				item.put("am_nowcredit", rs.getGeneralDouble("am_doublenowcredit"));
				item.put("cm_endbalance", rs.getGeneralDouble("cm_endamount"));
				item.put("am_endbalance", direct * (rs.getGeneralDouble("am_doubleenddebit") - rs.getGeneralDouble("am_doubleendcredit")));
			}
			store.add(item);
		}
		return store;
	}

	/**
	 * 合计
	 */
	private List<Map<String, Object>> getArCount(String sql, int yearmonth, String cateCode, boolean chkun) {
		SqlRowList rs = baseDao.queryForRowSet(sql.replace("@CODE", cateCode), yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("isCount", true);
			item.put("am_yearmonth", yearmonth);
			item.put("am_catecode", rs.getString("am_catecode"));
			item.put("am_currency", rs.getString("am_currency"));
			item.put("am_asscode", "合计");
			item.put("cm_beginbalance", rs.getGeneralDouble(1));
			item.put("am_beginbalance", rs.getGeneralDouble(2));
			item.put("cm_nowdebit", rs.getGeneralDouble(3));
			if (chkun)
				item.put("am_nowdebit", rs.getGeneralDouble(11));
			else
				item.put("am_nowdebit", rs.getGeneralDouble(4));
			item.put("cm_nowcredit", rs.getGeneralDouble(5));
			if (chkun)
				item.put("am_nowcredit", rs.getGeneralDouble(12));
			else
				item.put("am_nowcredit", rs.getGeneralDouble(6));
			item.put("cm_endbalance", rs.getGeneralDouble(7));
			if (chkun)
				item.put("am_endbalance", rs.getGeneralDouble(14));
			else
				item.put("am_endbalance", rs.getGeneralDouble(8));
			store.add(item);
		}
		return store;
	}

	@Override
	public void startAccount(int yearmonth, String module, String caller) {
		String procedureName = null;
		String result = null;
		Object[] args = new Object[] { yearmonth };
		if ("AR".equals(module)) {
			procedureName = "Sp_EndAr";
			result = "月应收结账成功";
		} else if ("AP".equals(module)) {
			procedureName = "Sp_EndAp";
			result = "月应付结账成功";
		} else if ("GL".equals(module)) {
			procedureName = "Sp_EndGl";
			result = "月总账结账成功";
		} else if ("CB".equals(module)) {
			procedureName = "SP_ENDBANK";
			result = "月银行票据结账成功";
			args = new Object[] { yearmonth, String.valueOf(SystemSession.getUser().getEm_name()) };
		} else if ("AS".equals(module)) {
			procedureName = "SP_ENDASSETS";
			result = "月固定资产结账成功";
		} else if ("CP".equals(module)) {
			procedureName = "SP_ENDCOST";
			result = "月项目成本结账成功";
		} else if ("ST".equals(module)) {
			procedureName = "Sp_EndProduct";
			result = "月库存结账成功";
		} else {
			BaseUtil.showError("结账模块错误！");
		}

		if (procedureName != null && result != null) {
			String res = baseDao.callProcedure(procedureName, args);
			if (!StringUtil.hasText(res) || res.equals("OK")) {
				baseDao.logger.others("结账操作", yearmonth + result, caller, "id", yearmonth);
			} else {
				BaseUtil.showError(res);
			}
		}

	}

	@Override
	public void overAccount(int yearmonth, String module, String caller) {
		String procedureName = null;
		String result = null;
		Object[] args = new Object[] { yearmonth };
		if ("AR".equals(module)) {
			procedureName = "Sp_UNEndAr";
			result = "月应收反结账成功";
		} else if ("AP".equals(module)) {
			procedureName = "Sp_UNEndAp";
			result = "月应付反结账成功";
		} else if ("GL".equals(module)) {
			procedureName = "Sp_UNEndGl";
			result = "月总账反结账成功";
		} else if ("CB".equals(module)) {
			procedureName = "SP_UNENDBANK";
			result = "月银行票据反结账成功";
			args = new Object[] { yearmonth, String.valueOf(SystemSession.getUser().getEm_name()) };
		} else if ("AS".equals(module)) {
			procedureName = "SP_UNENDASSETS";
			result = "月固定资产反结账成功";
		} else if ("CP".equals(module)) {
			procedureName = "Sp_UnEndCost";
			result = "月项目成本反结账成功";
		} else if ("ST".equals(module)) {
			procedureName = "Sp_UnEndProduct";
			result = "月库存反结账成功";
		} else {
			BaseUtil.showError("反结账模块错误！");
		}

		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_type='" + module + "'");
		if (first != null && yearmonth <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		if (procedureName != null && result != null) {
			String res = baseDao.callProcedure(procedureName, args);
			if (!StringUtil.hasText(res) || res.equals("OK")) {
				baseDao.logger.others("反结账操作", yearmonth + result, caller, "id", yearmonth);
			} else {
				BaseUtil.showError(res);
			}
		}
	}

	@Override
	public void startAccount(int yearmonth) {
		String res = baseDao.callProcedure("Sp_EndAr", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("结账操作", yearmonth + "月应收结账成功", "CheckAccount!AR", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void overAccount(int yearmonth) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-C'");
		if (first != null && yearmonth <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("Sp_UnEndAr", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("反结账操作", yearmonth + "月应收反结账成功", "CheckAccount!AR", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void startAccountAP(int yearmonth) {
		String res = baseDao.callProcedure("Sp_EndAp", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("结账操作", yearmonth + "月应付结账成功", "CheckAccountAP", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void overAccountAP(int yearmonth) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-V'");
		if (first != null && yearmonth <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("Sp_UnEndAp", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("反结账操作", yearmonth + "月应付反结账成功", "CheckAccountAP", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void startAccountGL(int yearmonth) {
		String res = baseDao.callProcedure("Sp_EndGl", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("结账操作", yearmonth + "月总账结账成功", "CheckAccount!GLA", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void overAccountGL(int yearmonth) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-A'");
		if (first != null && yearmonth <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("Sp_UnEndGl", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("反结账操作", yearmonth + "月总账反结账成功", "CheckAccount!GLA", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public List<Map<String, Object>> getApAccount(String condition) {
		JSONObject js = JSONObject.fromObject(condition);
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-V");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		// 包括未记账凭证
		boolean chkun = js.getBoolean("chkun");
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 应付账款科目
		String[] apCates = baseDao.getDBSettingArray("MonthAccount!AP", "apCatecode");
		if (apCates == null || apCates.length == 0) {
			BaseUtil.showError("未设置应收科目");
		}
		String[] ppCates = baseDao.getDBSettingArray("MonthAccount!AP", "prePayCatecode");
		String[] esCates = baseDao.getDBSettingArray("MonthAccount!AP", "esCatecode");
		// 执行预登账操作
		if (chkun) {
			String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { ym, ym, null, null });
			if (res != null && res.trim().length() > 0) {
				BaseUtil.showError(res);
			}
		}
		// 刷新数据
		String res = baseDao.callProcedure("SP_RefreshVendMonth", new Object[] { ym });
		if (StringUtil.hasText(res) && !res.equals("OK")) {
			BaseUtil.showError(res);
		}
		if (baseDao.isDBSetting("useBillOutAP")) {
			if (hasVendAsskind(apCates)) {
				store.addAll(getApData(AP1, ym, CollectionUtil.toSqlString(apCates), chkun, -1));
			} else {
				store.addAll(getApData(AP3, ym, CollectionUtil.toSqlString(apCates), chkun, -1));
			}

			if (ppCates != null && ppCates.length > 0) {
				if (hasVendAsskind(ppCates)) {
					store.addAll(getApData(AP_PRE, ym, CollectionUtil.toSqlString(ppCates), chkun, 1));// 预收的用
																										// 贷-借
				} else {
					store.addAll(getApData(AP_PRE1, ym, CollectionUtil.toSqlString(ppCates), chkun, 1));// 预收的用
				} // 贷-借
			}

			if (esCates != null && esCates.length > 0) {
				if (hasVendAsskind(esCates)) {
					store.addAll(getApData(AP_ES, ym, CollectionUtil.toSqlString(esCates), chkun, -1));
				} else {
					store.addAll(getApData(AP_ES1, ym, CollectionUtil.toSqlString(esCates), chkun, -1));
				}
			}
			if (hasVendAsskind(apCates)) {
				store.addAll(getApCount(AP_SUM1, ym, CollectionUtil.toSqlString(apCates), chkun));// 合计
			} else {
				store.addAll(getApCount(AP_SUM3, ym, CollectionUtil.toSqlString(apCates), chkun));// 合计
			}
			if (ppCates != null && ppCates.length > 0) {
				if (hasVendAsskind(ppCates)) {
					store.addAll(getApCount(AP_PRE_SUM, ym, CollectionUtil.toSqlString(ppCates), chkun));
				} else {
					store.addAll(getApCount(AP_PRE_SUM1, ym, CollectionUtil.toSqlString(ppCates), chkun));
				}
			}
			if (esCates != null && esCates.length > 0) {
				if (hasVendAsskind(esCates)) {
					store.addAll(getApCount(AP_ES_SUM, ym, CollectionUtil.toSqlString(esCates), chkun));
				} else {
					store.addAll(getApCount(AP_ES_SUM1, ym, CollectionUtil.toSqlString(esCates), chkun));
				}
			}
		} else {
			if (hasVendAsskind(apCates)) {
				store.addAll(getApData(AP, ym, CollectionUtil.toSqlString(apCates), chkun, -1));
			} else {
				store.addAll(getApData(AP2, ym, CollectionUtil.toSqlString(apCates), chkun, -1));
			}
			if (ppCates != null && ppCates.length > 0) {
				if (hasVendAsskind(ppCates)) {
					store.addAll(getApData(AP_PRE, ym, CollectionUtil.toSqlString(ppCates), chkun, 1));
				} else {
					store.addAll(getApData(AP_PRE1, ym, CollectionUtil.toSqlString(ppCates), chkun, 1));
				}
			}
			if (esCates != null && esCates.length > 0) {
				if (hasVendAsskind(esCates)) {
					store.addAll(getApData(AP_ES, ym, CollectionUtil.toSqlString(esCates), chkun, -1));
				} else {
					store.addAll(getApData(AP_ES1, ym, CollectionUtil.toSqlString(esCates), chkun, -1));
				}
			}
			if (hasVendAsskind(apCates)) {
				store.addAll(getApCount(AP_SUM, ym, CollectionUtil.toSqlString(apCates), chkun));// 合计
			} else {
				store.addAll(getApCount(AP_SUM2, ym, CollectionUtil.toSqlString(apCates), chkun));// 合计
			}
			if (ppCates != null && ppCates.length > 0)
				if (hasVendAsskind(ppCates)) {
					store.addAll(getApCount(AP_PRE_SUM, ym, CollectionUtil.toSqlString(ppCates), chkun));
				} else {
					store.addAll(getApCount(AP_PRE_SUM1, ym, CollectionUtil.toSqlString(ppCates), chkun));
				}
			if (esCates != null && esCates.length > 0)
				if (hasVendAsskind(esCates)) {
					store.addAll(getApCount(AP_ES_SUM, ym, CollectionUtil.toSqlString(esCates), chkun));
				} else {
					store.addAll(getApCount(AP_ES_SUM1, ym, CollectionUtil.toSqlString(esCates), chkun));
				}
		}
		return store;
	}

	private boolean hasVendAsskind(String[] codes) {
		int count = 0;
		for (String code : codes) {
			Object asstype = baseDao.getFieldDataByCondition("Category", "ca_asstype", "ca_code='" + code + "'");
			if (asstype == null) {
				return false;
			} else {
				String[] asstypes = asstype.toString().split("#");
				for (String type : asstypes) {
					if ("Vend".equals(type)) {
						count++;
						break;
					}
				}
			}
		}
		return count == codes.length;
	}

	private List<Map<String, Object>> getApData(String sql, int yearmonth, String cateCode, boolean chkun, int direct) {
		SqlRowList rs = baseDao.queryForRowSet(sql.replace("@CODE", cateCode), yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		Map<String, Object> item = null;
		while (rs.next()) {
			item = new HashMap<String, Object>();
			item.put("am_yearmonth", yearmonth);
			item.put("am_catecode", rs.getString("am_catecode"));
			item.put("am_asscode", rs.getString("am_asscode"));
			item.put("am_assname", rs.getString("am_assname"));
			item.put("am_currency", rs.getString("am_currency"));
			item.put("vm_beginbalance", rs.getGeneralDouble("vm_beginamount"));
			item.put("am_beginbalance", direct * (rs.getGeneralDouble("am_doublebegindebit") - rs.getGeneralDouble("am_doublebegincredit")));
			if (chkun) {
				item.put("vm_nowdebit", rs.getGeneralDouble("vm_payamount"));
				item.put("am_nowdebit", rs.getGeneralDouble("am_umdoublenowdebit"));
				item.put("vm_nowcredit", rs.getGeneralDouble("vm_nowamount"));
				item.put("am_nowcredit", rs.getGeneralDouble("am_umdoublenowcredit"));
				item.put("vm_endbalance", rs.getGeneralDouble("vm_endamount"));
				item.put("am_endbalance",
						direct * (rs.getGeneralDouble("am_umdoubleenddebit") - rs.getGeneralDouble("am_umdoubleendcredit")));
			} else {
				item.put("vm_nowdebit", rs.getGeneralDouble("vm_payamount"));
				item.put("am_nowdebit", rs.getGeneralDouble("am_doublenowdebit"));
				item.put("vm_nowcredit", rs.getGeneralDouble("vm_nowamount"));
				item.put("am_nowcredit", rs.getGeneralDouble("am_doublenowcredit"));
				item.put("vm_endbalance", rs.getGeneralDouble("vm_endamount"));
				item.put("am_endbalance", direct * (rs.getGeneralDouble("am_doubleenddebit") - rs.getGeneralDouble("am_doubleendcredit")));
			}
			store.add(item);
		}
		return store;
	}

	/**
	 * 合计
	 */
	private List<Map<String, Object>> getApCount(String sql, int yearmonth, String cateCode, boolean chkun) {
		SqlRowList rs = baseDao.queryForRowSet(sql.replace("@CODE", cateCode), yearmonth);
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		while (rs.next()) {
			Map<String, Object> item = new HashMap<String, Object>();
			item.put("isCount", true);
			item.put("am_yearmonth", yearmonth);
			item.put("am_catecode", rs.getString("am_catecode"));
			item.put("am_currency", rs.getString("am_currency"));
			item.put("am_asscode", "合计");
			item.put("vm_beginbalance", rs.getGeneralDouble(1));
			item.put("am_beginbalance", rs.getGeneralDouble(2));
			item.put("vm_nowdebit", rs.getGeneralDouble("vm_payamount"));
			if (chkun)
				item.put("am_nowdebit", rs.getGeneralDouble(11));
			else
				item.put("am_nowdebit", rs.getGeneralDouble(4));
			item.put("vm_nowcredit", rs.getGeneralDouble("vm_nowamount"));
			if (chkun)
				item.put("am_nowcredit", rs.getGeneralDouble(12));
			else
				item.put("am_nowcredit", rs.getGeneralDouble(6));
			item.put("vm_endbalance", rs.getGeneralDouble(7));
			if (chkun)
				item.put("am_endbalance", rs.getGeneralDouble(13));
			else
				item.put("am_endbalance", rs.getGeneralDouble(8));
			store.add(item);
		}
		return store;
	}

	@Override
	public void startAccount(Integer param) {
		String res = null;
		res = baseDao.callProcedure("SP_ENDBANK", new Object[] { param, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("OK")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("结账操作", param + "月银行票据结账成功", "CheckAccount!GS", "id", param);
	}

	@Override
	public void overAccount(Integer param) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-B'");
		if (first != null && param <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("SP_UNENDBANK", new Object[] { param, String.valueOf(SystemSession.getUser().getEm_name()) });
		if (res != null && !res.trim().equals("OK")) {
			BaseUtil.showError(res);
		}
		baseDao.logger.others("反结账操作", param + "月银行票据反结账成功", "CheckAccount!GS", "id", param);
	}

	public List<Map<String, Object>> getFixAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-F");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 固定资产科目
		String[] fixCates = baseDao.getDBSettingArray("MonthAccount!AS", "fixCatecode");
		if (fixCates == null || fixCates.length == 0) {
			BaseUtil.showError("未设置固定资产科目");
		}
		String cateStr = CollectionUtil.toSqlString(fixCates);
		// 固定资产
		// 期初原值
		List<Map<String, Object>> beginOld = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED'  and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double beginOldCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED'  and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期初差值
		List<Map<String, Object>> beginDiff = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym, ym).getResultList();
		// 合计
		Double beginDiffCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and ac_accatecode in("
								+ cateStr + ")", Double.class, ym, ym);
		// 本期借方原值
		List<Map<String, Object>> nowOld = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double nowOldCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 本期借方差值
		List<Map<String, Object>> nowDiff = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym, ym).getResultList();
		// 合计
		Double nowDiffCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and  ac_accatecode in("
								+ cateStr + ")", Double.class, ym, ym);
		// 增加
		List<Map<String, Object>> add = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(nvl(dd_amount,0),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产增加单' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double addCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(dd_amount,0),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产增加单' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 减少
		List<Map<String, Object>> reduce = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(nvl(dd_amount,0),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double reduceCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(dd_amount,0),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末原值
		List<Map<String, Object>> endOld = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym).getResultList();
		// 合计
		Double endOldCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(ac_oldvalue,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and ac_accatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末差值
		List<Map<String, Object>> endDiff = baseDao
				.queryForRowSet(
						"select ac_accatecode catecode,sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and nvl(ac_accatecode,' ')<>' ' group by ac_accatecode",
						ym, ym).getResultList();
		// 合计
		Double endDiffCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(case de_class when '资产减少单' then 1 else -1 end*nvl(dd_amount,0),2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>? and de_statuscode = 'POSTED' and de_class in ('资产减少单','资产增加单') and ac_accatecode in("
								+ cateStr + ")", Double.class, ym, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select cm_catecode,round(nvl(cm_begindebit,0)-nvl(cm_begincredit,0),2) cm_beginamount,round(cm_nowdebit,2) cm_nowdebit, round(cm_nowcredit,2) cm_nowcredit, round(nvl(cm_enddebit,0)-nvl(cm_endcredit,0),2) cm_endamount,round(cm_umnowdebit,2) cm_umnowdebit, round(cm_umnowcredit,2) cm_umnowcredit, round(nvl(cm_umenddebit,0)-nvl(cm_umendcredit,0),2) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym).getResultList();
		// 合计
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(round(nvl(cm_begindebit,0)-nvl(cm_begincredit,0),2)) cm_beginamount,sum(round(cm_nowdebit,2)) cm_nowdebit,sum(round(cm_nowcredit,2)) cm_nowcredit,sum(round(nvl(cm_enddebit,0),2)-round(nvl(cm_endcredit,0),2)) cm_endamount,sum(round(cm_umnowdebit,2)) cm_umnowdebit,sum(round(cm_umnowcredit,2)) cm_umnowcredit,sum(round(nvl(cm_umenddebit,0),2)-round(nvl(cm_umendcredit,0),2)) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym);
		for (String cate : fixCates) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("yearmonth", ym);
			map.put("type", "固定资产");
			map.put("catecode", cate);
			for (Map<String, Object> m : beginOld) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : beginDiff) {
				if (cate.equals(m.get("CATECODE"))) {
					double beginold = map.get("beginamount") == null ? 0 : Double.parseDouble(map.get("beginamount").toString());
					double begindiff = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("beginamount", beginold + begindiff);
					break;
				}
			}
			for (Map<String, Object> m : nowOld) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : nowDiff) {
				if (cate.equals(m.get("CATECODE"))) {
					double nowold = map.get("nowdebit") == null ? 0 : Double.parseDouble(map.get("nowdebit").toString());
					double nowdiff = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("nowdebit", nowold + nowdiff);
					break;
				}
			}
			for (Map<String, Object> m : add) {
				if (cate.equals(m.get("CATECODE"))) {
					double nowdebit = map.get("nowdebit") == null ? 0 : Double.parseDouble(map.get("nowdebit").toString());
					double nowadd = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("nowdebit", nowdebit + nowadd);
					break;
				}
			}
			for (Map<String, Object> m : reduce) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : endOld) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}

			for (Map<String, Object> m : endDiff) {
				if (cate.equals(m.get("CATECODE"))) {
					double endold = map.get("endamount") == null ? 0 : Double.parseDouble(map.get("endamount").toString());
					double enddiff = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("endamount", endold + enddiff);
					break;
				}
			}
			for (Map<String, Object> m : cm) {
				if (cate.equals(m.get("CM_CATECODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("type", "固定资产");
		map.put("catecode", "合计");
		map.put("beginamount", (beginOldCount == null ? 0 : beginOldCount) + (beginDiffCount == null ? 0 : beginDiffCount));
		map.put("nowdebit", (nowOldCount == null ? 0 : nowOldCount) + (nowDiffCount == null ? 0 : nowDiffCount)
				+ (addCount == null ? 0 : addCount));
		map.put("nowcredit", (reduceCount == null ? 0 : reduceCount));
		map.put("endamount", (endOldCount == null ? 0 : endOldCount) + (endDiffCount == null ? 0 : endDiffCount));
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getObject("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}

	@Override
	public List<Map<String, Object>> getDepreAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-F");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 累计折旧科目
		String[] deCates = baseDao.getDBSettingArray("MonthAccount!AS", "deCatecode");
		if (deCates == null || deCates.length == 0) {
			BaseUtil.showError("未设置累计折旧科目");
		}
		String cateStr = CollectionUtil.toSqlString(deCates);
		// 累计折旧
		// 期初原值
		List<Map<String, Object>> beginOld = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(nvl(ac_totaldepreciation,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED'  and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double beginOldCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(ac_totaldepreciation,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED'  and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期初差值
		List<Map<String, Object>> beginDiff = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(case de_class when '折旧单' then round(nvl(dd_amount,0),2)*-1 else round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2) end,2))  amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in('资产减少单','折旧单') and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym, ym).getResultList();
		// 合计
		Double beginDiffCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(case de_class when '折旧单' then round(nvl(dd_amount,0),2)*-1 else round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2) end,2))  amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>=? and de_statuscode = 'POSTED' and de_class in('资产减少单','折旧单') and  ac_ascatecode in("
								+ cateStr + ")", Double.class, ym, ym);
		// 本期借
		List<Map<String, Object>> debit = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED'  and to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym, ym).getResultList();
		// 合计
		Double debitCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='资产减少单' and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 本期贷
		List<Map<String, Object>> credit = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and nvl(ac_ascatecode,' ')<>' ' and de_class='折旧单' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double creditCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(dd_amount,2)) amount from AssetsDepreciation left join AssetsDepreciationDetail on de_id=dd_deid left join AssetsCard on dd_accode=ac_code where to_char(de_date,'yyyymm')=? and de_statuscode='POSTED' and de_class='折旧单' and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末原值
		List<Map<String, Object>> endOld = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(nvl(ac_totaldepreciation,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED'  and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym).getResultList();
		// 合计
		Double endOldCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(nvl(ac_totaldepreciation,0),2)) amount from assetscard where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED'  and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym);
		// 期末差值
		List<Map<String, Object>> endDiff = baseDao
				.queryForRowSet(
						"select ac_ascatecode catecode,sum(round(case de_class when '折旧单' then round(nvl(dd_amount,0),2)*-1 else round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2) end,2))  amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>? and de_statuscode = 'POSTED' and de_class in('资产减少单','折旧单') and nvl(ac_ascatecode,' ')<>' ' group by ac_ascatecode",
						ym, ym).getResultList();
		// 合计
		Double endDiffCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(round(case de_class when '折旧单' then round(nvl(dd_amount,0),2)*-1 else round(dd_totaldepreciation*dd_amount/nvl(dd_oldvalue,1),2) end,2)) amount from assetscard left join AssetsDepreciationDetail on ac_code =dd_accode inner join  AssetsDepreciation on dd_deid = de_id where to_char(ac_date,'yyyymm')<=? and ac_statuscode='AUDITED' and to_char(de_date,'yyyymm')>? and de_statuscode = 'POSTED' and de_class in('资产减少单','折旧单') and ac_ascatecode in("
								+ cateStr + ")", Double.class, ym, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select cm_catecode,round(nvl(cm_begincredit,0)-nvl(cm_begindebit,0),2) cm_beginamount,cm_nowdebit,cm_nowcredit,round(nvl(cm_endcredit,0)-nvl(cm_enddebit,0),2) cm_endamount,cm_umnowdebit,cm_umnowcredit,round(nvl(cm_umendcredit,0)-nvl(cm_umenddebit,0),2) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym).getResultList();// 贷-借
		// 合计
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(round(nvl(cm_begincredit,0)-nvl(cm_begindebit,0),2)) cm_beginamount,sum(round(cm_nowdebit,2)) cm_nowdebit,sum(round(cm_nowcredit,2)) cm_nowcredit,sum(round(nvl(cm_endcredit,0)-nvl(cm_enddebit,0),2)) cm_endamount,sum(round(cm_umnowdebit,2)) cm_umnowdebit,sum(round(cm_umnowcredit,2)) cm_umnowcredit,sum(round(nvl(cm_umendcredit,0)-nvl(cm_umenddebit,0),2)) cm_umendamount from catemonth where cm_yearmonth=? and cm_catecode in ("
								+ cateStr + ")", ym);
		for (String cate : deCates) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("yearmonth", ym);
			map.put("type", "累计折旧");
			map.put("catecode", cate);
			for (Map<String, Object> m : beginOld) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : beginDiff) {
				if (cate.equals(m.get("CATECODE"))) {
					double beginold = map.get("beginamount") == null ? 0 : Double.parseDouble(map.get("beginamount").toString());
					double begindiff = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("beginamount", beginold + begindiff);
					break;
				}
			}
			for (Map<String, Object> m : debit) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : credit) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : endOld) {
				if (cate.equals(m.get("CATECODE"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}

			for (Map<String, Object> m : endDiff) {
				if (cate.equals(m.get("CATECODE"))) {
					double endold = map.get("endamount") == null ? 0 : Double.parseDouble(map.get("endamount").toString());
					double enddiff = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("endamount", endold + enddiff);
					break;
				}
			}

			for (Map<String, Object> m : cm) {
				if (cate.equals(m.get("CM_CATECODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("type", "累计折旧");
		map.put("catecode", "合计");
		map.put("beginamount", (beginOldCount == null ? 0 : beginOldCount) + (beginDiffCount == null ? 0 : beginDiffCount));
		map.put("nowdebit", debitCount);
		map.put("nowcredit", creditCount);
		map.put("endamount", (endOldCount == null ? 0 : endOldCount) + (endDiffCount == null ? 0 : endDiffCount));
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getObject("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}

	static final String BANK = "select CA_CODE,CA_DESCRIPTION,AM_CURRENCY,AM_BEGINAMOUNT,AM_NOWDEPOSIT,AM_NOWPAYMENT,AM_NOWBALANCE,CM_BEGINAMOUNT,CM_NOWDEBIT,CM_NOWCREDIT,CM_ENDAMOUNT,CM_UMNOWDEBIT,CM_UMNOWCREDIT,CM_UMENDAMOUNT "
			+ "from (select case when NVL(AM_CATECODE,' ')=' ' then CM_CATECODE else AM_CATECODE end AM_CATECODE,case when NVL(AM_CURRENCY,' ')=' ' then CMC_CURRENCY else AM_CURRENCY end AM_CURRENCY,"
			+ "case when NVL(AM_YEARMONTH,0)=0 then CMC_YEARMONTH else AM_YEARMONTH end AM_YEARMONTH,NVL(AM_BEGINAMOUNT,0) AM_BEGINAMOUNT,NVL(AM_NOWDEPOSIT,0) AM_NOWDEPOSIT,NVL(AM_NOWPAYMENT,0) AM_NOWPAYMENT,"
			+ "NVL(AM_NOWBALANCE,0) AM_NOWBALANCE,NVL(CM_BEGINAMOUNT,0) CM_BEGINAMOUNT,NVL(CM_NOWDEBIT,0) CM_NOWDEBIT,NVL(CM_NOWCREDIT,0) CM_NOWCREDIT,NVL(CM_ENDAMOUNT,0) CM_ENDAMOUNT,nvl(CM_UMNOWDEBIT,0) CM_UMNOWDEBIT,"
			+ "nvl(CM_UMNOWCREDIT,0) CM_UMNOWCREDIT,nvl(CM_UMENDAMOUNT,0) CM_UMENDAMOUNT from (select ca_code am_catecode, am_currency, am_yearmonth, round(sum(am_beginamount),2) am_beginamount,round(sum(am_nowdeposit),2) am_nowdeposit,"
			+ "round(sum(am_nowpayment),2) am_nowpayment, round(sum(am_nowbalance),2) am_nowbalance from almonth left join category on am_accountcode=ca_id where (abs(ca_isbank)=1 or abs(ca_iscash)=1) group by ca_code,ca_name,am_currency,am_yearmonth) "
			+ "full join (select CMC_CATECODE CM_CATECODE, CMC_CURRENCY,CMC_YEARMONTH, ROUND(SUM(CMC_DOUBLEBEGINDEBIT-CMC_DOUBLEBEGINCREDIT),2) CM_BEGINAMOUNT,ROUND(SUM(CMC_DOUBLENOWDEBIT),2) CM_NOWDEBIT,ROUND(SUM(CMC_DOUBLENOWCREDIT),2) CM_NOWCREDIT,"
			+ "ROUND(SUM(CMC_DOUBLEENDDEBIT-CMC_DOUBLEENDCREDIT),2) CM_ENDAMOUNT,ROUND(SUM(CMC_UMDOUBLENOWDEBIT),2) CM_UMNOWDEBIT,ROUND(SUM(CMC_UMDOUBLENOWCREDIT),2) CM_UMNOWCREDIT,ROUND(SUM(CMC_UMDOUBLEENDDEBIT-CMC_UMDOUBLEENDCREDIT),2) CM_UMENDAMOUNT "
			+ "from CATEMONTHCURRENCY left join category on CMC_CATECODE=CA_CODE where (ABS(CA_ISBANK)=1 or ABS(CA_ISCASH)=1) group by CMC_CATECODE,CMC_CURRENCY,CMC_YEARMONTH) on AM_CATECODE=CM_CATECODE and AM_CURRENCY=CMC_CURRENCY and CMC_YEARMONTH=AM_YEARMONTH) "
			+ "left join category on AM_CATECODE=ca_code where AM_YEARMONTH=? and nvl(ca_isleaf,0)<>0 order by CA_CODE";
	static final String BANK_SUM = "select am_currency,round(sum(am_beginamount),2) am_beginamount,round(sum(am_nowdeposit),2) am_nowdeposit,round(sum(am_nowpayment),2) am_nowpayment,round(sum(am_nowbalance),2) am_nowbalance,round(sum(cm_beginamount),2) cm_beginamount,round(sum(cm_nowdebit),2) cm_nowdebit,round(sum(cm_nowcredit),2) cm_nowcredit,round(sum(cm_endamount),2) cm_endamount,round(sum(cm_umnowdebit),2) cm_umnowdebit,round(sum(cm_umnowcredit),2) cm_umnowcredit,round(sum(cm_umendamount),2) cm_umendamount "
			+ "from (select ca_code,CA_DESCRIPTION,ca_isleaf,am_currency,am_beginamount,am_nowdeposit,am_nowpayment,am_nowbalance,cm_beginamount,cm_nowdebit,cm_nowcredit,cm_endamount,cm_umnowdebit,cm_umnowcredit,cm_umendamount from "
			+ "(select ca_code,CA_DESCRIPTION,ca_isleaf, am_currency, am_yearmonth, sum(am_beginamount) am_beginamount,sum(am_nowdeposit) am_nowdeposit, sum(am_nowpayment) am_nowpayment, sum(am_nowbalance) am_nowbalance from almonth left join category on am_accountcode=ca_id where (abs(ca_isbank)=1 or abs(ca_iscash)=1) group by ca_code,CA_DESCRIPTION,am_currency,am_yearmonth,ca_isleaf) "
			+ "full join (select cmc_catecode cm_catecode, cmc_currency,cmc_yearmonth, sum(cmc_doublebegindebit-cmc_doublebegincredit) cm_beginamount,sum(cmc_doublenowdebit) cm_nowdebit,sum(cmc_doublenowcredit) cm_nowcredit,sum(cmc_doubleenddebit-cmc_doubleendcredit) cm_endamount,sum(cmc_umdoublenowdebit) cm_umnowdebit,sum(cmc_umdoublenowcredit) cm_umnowcredit,sum(cmc_umdoubleenddebit-cmc_umdoubleendcredit) cm_umendamount from catemonthcurrency left join category on cmc_catecode=ca_code where (abs(ca_isbank)=1 or abs(ca_iscash)=1) "
			+ "group by cmc_catecode,cmc_currency,cmc_yearmonth) on ca_code=cm_catecode and am_currency=cmc_currency and cmc_yearmonth=am_yearmonth where am_yearmonth=? and nvl(ca_isleaf,0)<>0 order by ca_code) group by am_currency";

	@Override
	public List<Map<String, Object>> getBankAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-B");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		SqlRowList rs = baseDao.queryForRowSet(BANK, ym);
		while (rs.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("yearmonth", ym);
			map.put("type", "银行");
			map.put("code", rs.getObject("ca_code"));
			map.put("name", rs.getObject("ca_description"));
			map.put("currency", rs.getObject("am_currency"));
			map.put("beginamount", rs.getGeneralDouble("am_beginamount"));
			map.put("cm_beginamount", rs.getGeneralDouble("cm_beginamount"));
			map.put("nowdebit", rs.getGeneralDouble("am_nowdeposit"));
			map.put("nowcredit", rs.getGeneralDouble("am_nowpayment"));
			map.put("endamount", rs.getGeneralDouble("am_nowbalance"));
			if (chkun) {
				map.put("cm_nowdebit", rs.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", rs.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", rs.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", rs.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", rs.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", rs.getGeneralDouble("cm_endamount"));
			}
			store.add(map);
		}
		SqlRowList sum = baseDao.queryForRowSet(BANK_SUM, ym);
		while (sum.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("isCount", true);
			map.put("code", "合计");
			map.put("type", "银行");
			map.put("currency", sum.getObject("am_currency"));
			map.put("beginamount", sum.getGeneralDouble("am_beginamount"));
			map.put("cm_beginamount", sum.getGeneralDouble("cm_beginamount"));
			map.put("nowdebit", sum.getGeneralDouble("am_nowdeposit"));
			map.put("nowcredit", sum.getGeneralDouble("am_nowpayment"));
			map.put("endamount", sum.getGeneralDouble("am_nowbalance"));
			if (chkun) {
				map.put("cm_nowdebit", sum.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", sum.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", sum.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", sum.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", sum.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", sum.getGeneralDouble("cm_endamount"));
			}
			store.add(map);
		}
		return store;
	}

	@Override
	public List<Map<String, Object>> getBillArAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-B");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 应收票据科目
		String[] barCates = baseDao.getDBSettingArray("CheckAccount!GS", "barCatecode");
		if (barCates == null || barCates.length == 0) {
			BaseUtil.showError("未设置应收票据科目");
		}
		String cateStr = CollectionUtil.toSqlString(barCates);
		// 应收票据
		// 期初
		List<Map<String, Object>> begin = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(bar_leftamount) amount from billar where to_char(bar_date,'yyyymm') < ? and bar_statuscode='AUDITED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym).getResultList();
		Double beginCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bar_leftamount) amount from billar where to_char(bar_date,'yyyymm') < ? and bar_statuscode='AUDITED'",
				Double.class, ym);
		List<Map<String, Object>> beginChange = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')>=? and brc_statuscode='POSTED' and to_char(bar_date,'yyyymm') < ? and bar_statuscode='AUDITED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym, ym).getResultList();
		Double beginChangeCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')>=? and brc_statuscode='POSTED' and to_char(bar_date,'yyyymm') < ? and bar_statuscode='AUDITED'",
						Double.class, ym, ym);
		// 借
		List<Map<String, Object>> debit = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(bar_doublebalance) amount from billar where to_char(bar_date,'yyyymm') = ? and bar_statuscode='AUDITED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym).getResultList();
		Double debitCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bar_doublebalance) amount from billar where to_char(bar_date,'yyyymm') = ? and bar_statuscode='AUDITED'",
				Double.class, ym);
		// 贷
		List<Map<String, Object>> credit = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')=? and brc_statuscode='POSTED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym).getResultList();
		Double creditCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')=? and brc_statuscode='POSTED'",
						Double.class, ym);
		// 期末
		List<Map<String, Object>> end = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(bar_leftamount) amount from billar where to_char(bar_date,'yyyymm') <= ? and bar_statuscode='AUDITED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym).getResultList();
		Double endCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bar_leftamount) amount from billar where to_char(bar_date,'yyyymm') <= ? and bar_statuscode='AUDITED'",
				Double.class, ym);
		List<Map<String, Object>> endChange = baseDao
				.queryForRowSet(
						"select * from (select bar_currency,bar_custcode,bar_custname,sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')>? and brc_statuscode='POSTED' and to_char(bar_date,'yyyymm') <= ? and bar_statuscode='AUDITED' group by bar_currency,bar_custcode,bar_custname) order by bar_currency,bar_custcode,bar_custname",
						ym, ym).getResultList();
		Double endChangeCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(brd_amount) amount from billarchange left join billarchangedetail on brd_brcid=brc_id left join billar on brd_barcode=bar_code where to_char(brc_date,'yyyymm')>? and brc_statuscode='POSTED' and to_char(bar_date,'yyyymm') <= ? and bar_statuscode='AUDITED'",
						Double.class, ym, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select * from (select am_currency,am_asscode,am_assname,sum(am_doublebegindebit-am_doublebegincredit) cm_beginamount,sum(am_doublenowdebit) cm_nowdebit,sum(am_doublenowcredit) cm_nowcredit,sum(am_doubleenddebit-am_doubleendcredit) cm_endamount,sum(am_umdoublenowdebit) cm_umnowdebit,sum(am_umdoublenowcredit) cm_umnowcredit,sum(am_umdoubleenddebit-am_umdoubleendcredit) cm_umendamount from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ") group by am_currency,am_asscode,am_assname) order by am_currency,am_asscode,am_assname", ym)
				.getResultList();
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(am_doublebegindebit-am_doublebegincredit) cm_beginamount,sum(am_doublenowdebit) cm_nowdebit,sum(am_doublenowcredit) cm_nowcredit,sum(am_doubleenddebit-am_doubleendcredit) cm_endamount,sum(am_umdoublenowdebit) cm_umnowdebit,sum(am_umdoublenowcredit) cm_umnowcredit,sum(am_umdoubleenddebit-am_umdoubleendcredit) cm_umendamount from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ")", ym);
		// 所有使用到的币别+客户组合
		SqlRowList groups = baseDao
				.queryForRowSet(
						"select distinct * from (select distinct bar_currency,bar_custcode,bar_custname from billar where to_char(bar_date,'yyyymm') <= ? and bar_statuscode='AUDITED' union select distinct am_currency,am_asscode,am_assname from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ")) where nvl(bar_custcode,' ')<>' '  order by bar_currency,bar_custcode,bar_custname", ym, ym);
		while (groups.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String currency = groups.getString("bar_currency");
			String custcode = groups.getString("bar_custcode");
			map.put("yearmonth", ym);
			map.put("type", "应收票据");
			map.put("currency", currency);
			map.put("code", custcode);
			map.put("name", groups.getString("bar_custname"));
			for (Map<String, Object> m : begin) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : beginChange) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					double beginamount = map.get("beginamount") == null ? 0 : Double.parseDouble(map.get("beginamount").toString());
					double change = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("beginamount", beginamount + change);
					break;
				}
			}
			for (Map<String, Object> m : debit) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : credit) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : end) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : endChange) {
				if (currency.equals(m.get("BAR_CURRENCY")) && custcode.equals(m.get("BAR_CUSTCODE"))) {
					double endamount = map.get("endamount") == null ? 0 : Double.parseDouble(map.get("endamount").toString());
					double change = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("endamount", endamount + change);
					break;
				}
			}
			for (Map<String, Object> m : cm) {
				if (currency.equals(m.get("AM_CURRENCY")) && custcode.equals(m.get("AM_ASSCODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("code", "合计");
		map.put("type", "应收票据");
		map.put("beginamount", (beginCount == null ? 0 : beginCount) + (beginChangeCount == null ? 0 : beginChangeCount));
		map.put("nowdebit", debitCount);
		map.put("nowcredit", creditCount);
		map.put("endamount", (endCount == null ? 0 : endCount) + (endChangeCount == null ? 0 : endChangeCount));
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getGeneralDouble("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}

	@Override
	public List<Map<String, Object>> getBillApAccount(boolean chkun) {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-B");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		List<Map<String, Object>> store = new ArrayList<Map<String, Object>>();
		// 应付票据科目
		String[] bapCates = baseDao.getDBSettingArray("CheckAccount!GS", "bapCatecode");
		if (bapCates == null || bapCates.length == 0) {
			BaseUtil.showError("未设置应付票据科目");
		}
		String cateStr = CollectionUtil.toSqlString(bapCates);
		// 应付票据
		// 期初
		List<Map<String, Object>> begin = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bap_leftamount) amount from billap where to_char(bap_date,'yyyymm') < ? and bap_statuscode='AUDITED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym).getResultList();
		Double beginCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bap_leftamount) amount from billap where to_char(bap_date,'yyyymm') < ? and bap_statuscode='AUDITED'",
				Double.class, ym);
		List<Map<String, Object>> beginChange = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')>=? and bpc_statuscode='POSTED' and to_char(bap_date,'yyyymm') < ? and bap_statuscode='AUDITED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym, ym).getResultList();
		Double beginChangeCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')>=? and bpc_statuscode='POSTED' and to_char(bap_date,'yyyymm') < ? and bap_statuscode='AUDITED'",
						Double.class, ym, ym);
		// 借(应付票据异动借方总额)
		List<Map<String, Object>> debit = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')=? and bpc_statuscode='POSTED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym).getResultList();
		Double debitCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')=? and bpc_statuscode='POSTED'",
						Double.class, ym);

		// 贷(应付票据票据金额)
		List<Map<String, Object>> credit = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bap_doublebalance) amount from billap where to_char(bap_date,'yyyymm') = ? and bap_statuscode='AUDITED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym).getResultList();
		Double creditCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bap_doublebalance) amount from billap where to_char(bap_date,'yyyymm') = ? and bap_statuscode='AUDITED'",
				Double.class, ym);
		// 期末
		List<Map<String, Object>> end = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bap_leftamount) amount from billap where to_char(bap_date,'yyyymm') <= ? and bap_statuscode='AUDITED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym).getResultList();
		Double endCount = baseDao.getJdbcTemplate().queryForObject(
				"select sum(bap_leftamount) amount from billap where to_char(bap_date,'yyyymm') <= ? and bap_statuscode='AUDITED'",
				Double.class, ym);
		List<Map<String, Object>> endChange = baseDao
				.queryForRowSet(
						"select * from (select bap_currency,bap_vendcode,bap_vendname,sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')>? and bpc_statuscode='POSTED' and to_char(bap_date,'yyyymm') <= ? and bap_statuscode='AUDITED' group by bap_currency,bap_vendcode,bap_vendname) order by bap_currency,bap_vendcode,bap_vendname",
						ym, ym).getResultList();
		Double endChangeCount = baseDao
				.getJdbcTemplate()
				.queryForObject(
						"select sum(bpd_amount) amount from billapchange left join billapchangedetail on bpd_bpcid=bpc_id left join billap on bpd_bapcode=bap_code where to_char(bpc_date,'yyyymm')>? and bpc_statuscode='POSTED' and to_char(bap_date,'yyyymm') <= ? and bap_statuscode='AUDITED'",
						Double.class, ym, ym);
		// 总账
		List<Map<String, Object>> cm = baseDao
				.queryForRowSet(
						"select * from (select am_currency,am_asscode,am_assname,sum(am_doublebegincredit-am_doublebegindebit) cm_beginamount,sum(am_doublenowdebit) cm_nowdebit,sum(am_doublenowcredit) cm_nowcredit,sum(am_doubleendcredit-am_doubleenddebit) cm_endamount,sum(am_umdoublenowdebit) cm_umnowdebit,sum(am_umdoublenowcredit) cm_umnowcredit,sum(am_umdoubleendcredit-am_umdoubleenddebit) cm_umendamount from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ") group by am_currency,am_asscode,am_assname) order by am_currency,am_asscode,am_assname", ym)
				.getResultList();// credit - debit
		SqlRowList cmCount = baseDao
				.queryForRowSet(
						"select sum(am_doublebegincredit-am_doublebegindebit) cm_beginamount,sum(am_doublenowdebit) cm_nowdebit,sum(am_doublenowcredit) cm_nowcredit,sum(am_doubleendcredit-am_doubleenddebit) cm_endamount,sum(am_umdoublenowdebit) cm_umnowdebit,sum(am_umdoublenowcredit) cm_umnowcredit,sum(am_umdoubleendcredit-am_umdoubleenddebit) cm_umendamount from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ")", ym);
		// 所有使用到的币别+供应商组合
		SqlRowList groups = baseDao
				.queryForRowSet(
						"select distinct * from (select distinct bap_currency,bap_vendcode,bap_vendname from billap where to_char(bap_date,'yyyymm') <= ? and bap_statuscode='AUDITED' union select distinct am_currency,am_asscode,am_assname from assmonth where am_yearmonth=? and am_catecode in ("
								+ cateStr + ")) where nvl(bap_vendcode,' ')<>' ' order by bap_currency,bap_vendcode,bap_vendname", ym, ym);
		while (groups.next()) {
			Map<String, Object> map = new HashMap<String, Object>();
			String currency = groups.getString("bap_currency");
			String custcode = groups.getString("bap_vendcode");
			map.put("yearmonth", ym);
			map.put("type", "应付票据");
			map.put("currency", currency);
			map.put("code", custcode);
			map.put("name", groups.getString("bap_vendname"));
			for (Map<String, Object> m : begin) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					map.put("beginamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : beginChange) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					double beginamount = map.get("beginamount") == null ? 0 : Double.parseDouble(map.get("beginamount").toString());
					double change = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("beginamount", beginamount + change);
					break;
				}
			}
			for (Map<String, Object> m : debit) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					map.put("nowdebit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : credit) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					map.put("nowcredit", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : end) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					map.put("endamount", m.get("AMOUNT"));
					break;
				}
			}
			for (Map<String, Object> m : endChange) {
				if (currency.equals(m.get("bap_currency")) && custcode.equals(m.get("bap_vendcode"))) {
					double endamount = map.get("endamount") == null ? 0 : Double.parseDouble(map.get("endamount").toString());
					double change = m.get("AMOUNT") == null ? 0 : Double.parseDouble(m.get("AMOUNT").toString());
					map.put("endamount", endamount + change);
					break;
				}
			}
			for (Map<String, Object> m : cm) {
				if (currency.equals(m.get("AM_CURRENCY")) && custcode.equals(m.get("AM_ASSCODE"))) {
					map.put("cm_beginamount", m.get("CM_BEGINAMOUNT"));
					if (chkun) {
						map.put("cm_nowdebit", m.get("CM_UMNOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_UMNOWCREDIT"));
						map.put("cm_endamount", m.get("CM_UMENDAMOUNT"));
					} else {
						map.put("cm_nowdebit", m.get("CM_NOWDEBIT"));
						map.put("cm_nowcredit", m.get("CM_NOWCREDIT"));
						map.put("cm_endamount", m.get("CM_ENDAMOUNT"));
					}
					break;
				}
			}
			store.add(map);
		}
		// 合计
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("isCount", true);
		map.put("code", "合计");
		map.put("type", "应付票据");
		map.put("beginamount", (beginCount == null ? 0 : beginCount) + (beginChangeCount == null ? 0 : beginChangeCount));
		map.put("nowdebit", debitCount);
		map.put("nowcredit", creditCount);
		map.put("endamount", (endCount == null ? 0 : endCount) + (endChangeCount == null ? 0 : endChangeCount));
		if (cmCount.next()) {
			map.put("cm_beginamount", cmCount.getGeneralDouble("cm_beginamount"));
			if (chkun) {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_umnowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_umnowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_umendamount"));
			} else {
				map.put("cm_nowdebit", cmCount.getGeneralDouble("cm_nowdebit"));
				map.put("cm_nowcredit", cmCount.getGeneralDouble("cm_nowcredit"));
				map.put("cm_endamount", cmCount.getGeneralDouble("cm_endamount"));
			}
		}
		store.add(map);
		return store;
	}

	@Override
	public void preWriteVoucher() {
		Map<String, Object> periods = voucherDao.getJustPeriods("Month-A");
		int ym = Integer.parseInt(periods.get("PD_DETNO").toString());
		String res = baseDao.callProcedure("Sp_PreWriteVoucher", new Object[] { ym, ym, null, null });
		if (res != null && res.trim().length() > 0) {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void startAccountPLM(int yearmonth) {
		String res = baseDao.callProcedure("SP_ENDPROJECTCOST", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("结账操作", yearmonth + "月项目成本结账成功", "MonthAccountOver!PRJCOST", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void overAccountPLM(int yearmonth) {
		Object first = baseDao.getFieldDataByCondition("periods", "pe_firstday", "pe_code='MONTH-O'");
		if (first != null && yearmonth <= Integer.parseInt(first.toString())) {
			BaseUtil.showError("当前期间小于等于初始化期间，不能反结账！");
		}
		String res = baseDao.callProcedure("SP_UNENDPORJECTCOST", new Object[] { yearmonth });
		if (res.equals("OK")) {
			baseDao.logger.others("反结账操作", yearmonth + "月项目成本反结账成功", "MonthAccountOver!PRJCOST", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void getShareRate(int yearmonth) {
		String res = baseDao.callProcedure("CACSHARERATE", new Object[] { yearmonth });
		if (res.equals("OK")) {
			BaseUtil.appendError("分摊系数获取成功，可以通过【分摊系数设置】查看、修改");
			baseDao.logger.others("获取分摊系数", "获取分摊系数成功", "GetShareRate", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void createShareVoucher(int yearmonth) {
		String res = baseDao.callProcedure("CACSHAREFEE", new Object[] { yearmonth, SystemSession.getUser().getEm_id(),
				SystemSession.getUser().getEm_name() });
		if (res.equals("OK")) {
			BaseUtil.appendError("费用部门分摊凭证已生成，请到总账凭证列表查看");
			baseDao.logger.others("生成分摊凭证", "生成分摊凭证成功", "CreateShareVoucher", "id", yearmonth);
		} else {
			BaseUtil.showError(res);
		}
	}

	@Override
	public void refreshEndData(String mould) {
		Map<String, Object> periods = voucherDao.getJustPeriods("MONTH-B");
		checkAccountService.refreshEndData(periods.get("PD_DETNO").toString(), mould);
	}
}

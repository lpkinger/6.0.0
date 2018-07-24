package com.uas.erp.dao.common.impl;

import net.sf.json.JSONObject;

import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.CustMonthDao;
import com.uas.erp.model.Employee;

@Repository
public class CustMonthDaoImpl extends BaseDao implements CustMonthDao {
	static final String TURNCUSTMONTH = "SELECT * FROM CustMonth WHERE cm_id=?";
	static final String INSERTRECBALANCE = "insert into recbalance ("
			+ "rb_id,rb_code,rb_custid,rb_custcode,rb_custname,rb_date,rb_currency,rb_rate,"
			+ "rb_seller,rb_amount,rb_status,rb_emname,rb_auditstatus,rb_recorddate,"
			+ "rb_cmcurrency,rb_cmrate,rb_cmamount,rb_strikestatus,rb_statuscode,"
			+ "rb_auditstatuscode,rb_sellerid,rb_strikestatuscode,rb_emid,rb_kind,"
			+ "rb_sourceid,rb_beginlast,rb_aramount) values(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?)";

	@Override
	public JSONObject turnRecBalance(int id, Double balance, Double cm_prepayend, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNCUSTMONTH, new Object[] { id });
		boolean confirm = checkIf("CONFIRMPREREC", "CPR_CMID=" + id) && checkIf("CONFIRMARBILL", "CAR_CMID=" + id);
		if (rs.next()) {
			if (confirm) {
				double amount1 = getSummaryByField("CONFIRMPREREC", "round(NVL(CPR_AMOUNY,0),2)", "CPR_CMID=" + id);
				double amount2 = getSummaryByField("CONFIRMARBILL", "round(NVL(CAR_AMOUNY,0),2)", "CAR_CMID=" + id);
				balance = NumberUtil.formatDouble(balance, 2);
				amount1 = NumberUtil.formatDouble(amount1, 2);
				amount2 = NumberUtil.formatDouble(amount2, 2);
				if (balance != amount1) {
					BaseUtil.showError("所选预收金额合计[" + amount1 + "]与填写冲账金额[" + balance + "]不相等！请重新选择！");
				}
				if (balance != amount2) {
					BaseUtil.showError("所选发票金额合计[" + amount2 + "]与填写冲账金额[" + balance + "]不相等！！请重新选择！");
				}
			}
			Object date = getFieldDataByCondition("PERIODSDETAIL", "PD_ENDDATE", "PD_DETNO = '" + rs.getObject("cm_yearmonth") + "'");
			int rbid = getSeqId("RECBALANCE_SEQ");
			String code = sGetMaxNumber("RecBalance", 2);
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rs.getObject("cm_currency") + "'");
			Object[] cust = getFieldsDataByCondition("Customer", new String[] { "cu_name", "cu_id", "cu_sellername", "cu_sellerid" },
					"cu_code='" + rs.getObject("cm_custcode") + "'");
			execute(INSERTRECBALANCE,
					new Object[] { rbid, code, cust[1], rs.getObject("cm_custcode"), cust[0], date, rs.getObject("cm_currency"), rate,
							cust[2], balance, BaseUtil.getLocalMessage("UNPOST", language), employee.getEm_name(),
							BaseUtil.getLocalMessage("ENTERING", language), rs.getObject("cm_currency"), 1, balance,
							BaseUtil.getLocalMessage("UNSTRIKE", language), "UNPOST", "ENTERING", cust[3], "UNSTRIKE", employee.getEm_id(),
							"预收冲应收", rs.getObject("cm_id"), cm_prepayend, balance });
			if (isDBSetting("CustMonth!Cys!Batch", "isSysdate")) {
				execute("update RECBALANCE set rb_date=sysdate where rb_id=" + rbid);
			}
			if (confirm) {
				execute("insert into RECBALANCEPRDETAIL (rbpd_id,rbpd_rbid,rbpd_detno,rbpd_ordercode,rbpd_date,rbpd_currency,rbpd_recorder,rbpd_vouchercode,rbpd_amount,rbpd_havebalance,rbpd_nowbalance,rbpd_voucherid,rbpd_recorderid,rbpd_sourceid)"
						+ " select RECBALANCEPRDETAIL_SEQ.NEXTVAL,"
						+ rbid
						+ ",rownum,PR_CODE,PR_DATE,pr_cmcurrency,pr_recorder,pr_vouchercode,pr_jsamount,pr_havebalance,round(CPR_AMOUNY,2),pr_voucherid,pr_recorderid,pr_id from CONFIRMPREREC left join PreREC on CPR_PRID=PR_ID where CPR_CMID="
						+ id);
				execute("insert into RecBalanceDetail (rbd_id,rbd_rbid,rbd_detno,rbd_ordercode,rbd_invoicedate,rbd_duedate,rbd_currency,rbd_seller,rbd_aramount,rbd_havepay,rbd_nowbalance,rbd_sellerid,rbd_status,rbd_catecode,rbd_catename,rbd_cateid,rbd_orderid)"
						+ " select RECBALANCEDETAIL_SEQ.NEXTVAL,"
						+ rbid
						+ ",rownum,AB_CODE,ab_date,ab_paydate,ab_currency,ab_seller,ab_aramount,ab_payamount,round(CAR_AMOUNY,2),ab_sellerid,0,ab_catecode,ab_catename,ab_cateid,ab_id from CONFIRMARBILL left join ARBILL on CAR_ABID=AB_id where CAR_CMID="
						+ id);
				execute("delete from CONFIRMPREREC where CPR_CMID=" + id);
				execute("delete from CONFIRMARBILL where CAR_CMID=" + id);
			}
			JSONObject j = new JSONObject();
			j.put("rb_id", rbid);
			j.put("rb_code", code);
			return j;
		} else {
			return null;
		}
	}
}

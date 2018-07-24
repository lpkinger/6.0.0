package com.uas.erp.dao.common.impl;

import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.NumberUtil;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.dao.SqlRowList;
import com.uas.erp.dao.common.VendMonthDao;
import com.uas.erp.dao.common.VoucherDao;
import com.uas.erp.model.Employee;

@Repository
public class VendMonthDaoImpl extends BaseDao implements VendMonthDao {
	static final String TURNVENDMONTH = "SELECT * FROM VendMonth WHERE vm_id=?";
	static final String INSERTPAYBALANCE = "insert into paybalance ("
			+ "pb_id,pb_code,pb_vendid,pb_vendcode,pb_vendname,pb_date,pb_currency,pb_rate,"
			+ "pb_buyer,pb_amount,pb_status,pb_recorder,pb_auditstatus,pb_recorddate,"
			+ "pb_vmcurrency,pb_vmrate,pb_prepayamount,pb_vmamount,pb_vmstatus,pb_statuscode,"
			+ "pb_auditstatuscode,pb_buyerid,pb_vmstatuscode,pb_recorderid,pb_kind,"
			+ "pb_sourceid,pb_jsamount) values(?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate,?,?,?,?,?,?,?,?,?,?,?,?,?)";
	@Autowired
	private VoucherDao voucherDao;

	@Override
	public JSONObject turnPayBalance(String id, double balance, String language, Employee employee) {
		SqlRowList rs = queryForRowSet(TURNVENDMONTH, new Object[] { id });
		boolean confirm = checkIf("CONFIRMPREPAY", "CPP_VMID=" + id) && checkIf("CONFIRMAPBILL", "CAP_VMID=" + id);
		if (rs.next()) {
			if (confirm) {
				double amount1 = getSummaryByField("CONFIRMPREPAY", "NVL(CPP_AMOUNY,0)", "CPP_VMID=" + id);
				double amount2 = getSummaryByField("CONFIRMAPBILL", "NVL(CAP_AMOUNY,0)", "CAP_VMID=" + id);
				balance = NumberUtil.formatDouble(balance, 2);
				amount1 = NumberUtil.formatDouble(amount1, 2);
				amount2 = NumberUtil.formatDouble(amount2, 2);
				if (balance != amount1) {
					BaseUtil.showError("所选预付金额合计[" + amount1 + "]与填写冲账金额[" + balance + "]不相等！请重新选择！");
				}
				if (balance != amount2) {
					BaseUtil.showError("所选发票金额合计[" + amount2 + "]与填写冲账金额[" + balance + "]不相等！！请重新选择！");
				}
			}
			int pbid = getSeqId("PAYBALANCE_SEQ");
			String code = sGetMaxNumber("PayBalance", 2);
			Object rate = getFieldDataByCondition("Currencys", "cr_rate", "cr_name='" + rs.getObject("vm_currency") + "'");
			Object[] vend = getFieldsDataByCondition("Vendor", new String[] { "ve_name", "ve_id", "ve_buyername", "nvl(ve_buyerid,0)" },
					"ve_code='" + rs.getObject("vm_vendcode") + "'");
			Map<String, Object> period = voucherDao.getJustPeriods("MONTH-V");
			execute(INSERTPAYBALANCE,
					new Object[] { pbid, code, vend[1], rs.getObject("vm_vendcode"), vend[0], period.get("PD_ENDDATE"),
							rs.getObject("vm_currency"), rate, vend[2], balance, BaseUtil.getLocalMessage("UNPOST", language),
							employee.getEm_name(), BaseUtil.getLocalMessage("ENTERING", language), rs.getObject("vm_currency"), 1,
							rs.getObject("vm_prepayend"), balance, BaseUtil.getLocalMessage("UNSTRIKE", language), "UNPOST", "ENTERING",
							vend[3], "UNSTRIKE", employee.getEm_id(), "预付冲应付", rs.getObject("vm_id"), balance });
			if (confirm) {
				execute("insert into PAYBALANCEPRDETAIL (pbpd_id,pbpd_pbid,pbpd_detno,pbpd_ordercode,pbpd_date,pbpd_currency,pbpd_recorder,pbpd_vouchercode,pbpd_amount,pbpd_havebalance,pbpd_nowbalance,pbpd_voucherid,pbpd_recorderid,pbpd_sourceid)"
						+ " select PAYBALANCEPRDETAIL_SEQ.NEXTVAL,"
						+ pbid
						+ ",rownum,PP_CODE,PP_DATE,pp_vmcurrency,pp_recorder,pp_vouchercode,pp_jsamount,pp_havebalance,round(CPP_AMOUNY,2),pp_voucherid,pp_recorderid,pp_id from CONFIRMPREPAY left join PrePay on CPP_PPID=pp_id where CPP_VMID="
						+ id);
				execute("insert into PayBalanceDetail (pbd_id,pbd_pbid,pbd_detno,pbd_ordercode,pbd_duedate,pbd_currency,pbd_buyer,pbd_apamount,pbd_havepay,pbd_nowbalance,pbd_buyerid,pbd_code,pbd_ordertype,pbd_status,pbd_orderdate,pbd_catecode,pbd_catename,pbd_cateid)"
						+ " select PAYBALANCEDETAIL_SEQ.NEXTVAL,"
						+ pbid
						+ ",rownum,AB_CODE,ab_paydate,ab_currency,ab_buyer,ab_apamount,ab_payamount,round(CAP_AMOUNY,2),ab_buyerid,'"
						+ code
						+ "',ab_class,0,AB_DATE,ab_catecode,ab_catename,ab_cateid from CONFIRMAPBILL left join APBILL on CAP_ABID=AB_id where CAP_VMID="
						+ id);
				execute("delete from CONFIRMPREPAY where CPP_VMID=" + id);
				execute("delete from CONFIRMAPBILL where CAP_VMID=" + id);
			}
			JSONObject j = new JSONObject();
			j.put("pb_id", pbid);
			j.put("pb_code", code);
			return j;
		} else {
			return null;
		}
	}
}

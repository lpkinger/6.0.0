package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface PayPleaseDao {
	JSONObject turnPayBalance(int ppd_id, String sourcecode, Object amount);

	JSONObject turnPrePay(int ppd_id, String sourcecode, Object amount);

	JSONObject turnBillAP(int id);

	JSONObject turnBankRegister(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno);

	JSONObject turnBillAP(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno);

	JSONObject turnBillARChange(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno);

	JSONObject turnPayBalanceCYF(int ppd_id, String sourcecode, String type, Object amount, String date, String checkno);

	// 更新付款申请明细行一、二已转金额
	void updateDetailAmount(Object pp_code);

	// 更新预付款申请明细行一、二已转金额
	void updateDetailAmountYF(Object pp_code);
}

package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface PayPleaseService {
	void savePayPlease(String caller, String formStore, String param1, String param2);

	void updatePayPleaseById(String caller, String formStore, String param1, String param2);

	void deletePayPlease(String caller, int pp_id);

	void auditPayPlease(int pp_id, String caller);

	void resAuditPayPlease(int pp_id, String caller);

	void submitPayPlease(int pp_id, String caller);

	void resSubmitPayPlease(int pp_id, String caller);

	JSONObject turnPrePay(String caller, String formStore);

	JSONObject turnBankRegister(String caller, String formStore);

	JSONObject turnBillAP(String caller, String formStore);

	JSONObject turnBillARChange(String caller, String formStore);

	JSONObject turnPayBalanceCYF(String caller, String formStore);

	void catchAP(String caller, String ppd_id, String ppd_ppid, String startdate, String enddate, String bicode);

	void cleanAP(String caller, String ppd_id, String ppd_ppid);

	String[] printPayPlease(int pp_id, String reportName, String condition, String caller);

	void endPayPlease(int pp_id, String caller);

	void resEndPayPlease(int pp_id, String caller);

	void reLockAmount(int id, String abcode, Double amount);

}

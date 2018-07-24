package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface APCheckService {
	void saveAPCheck(String formStore, String gridStore);

	void updateAPCheckById(String formStore, String gridStore);

	void deleteAPCheck(int ac_id);

	String[] printAPCheck(int ac_id, String reportName, String condition);

	void auditAPCheck(int ac_id);

	void resAuditAPCheck(int ac_id);

	void submitAPCheck(int ac_id);

	void resSubmitAPCheck(int ac_id);

	void accountedAPCheck(int ac_id);

	void resAccountedAPCheck(int ac_id);

	void confirmAPCheck(int ac_id);

	void cancelAPCheck(int ac_id);

	void resConfirmAPCheck(int id, String reason);

	String turnBill(String caller, String data);

	JSONObject turnPayPlease(int id, String caller);

	void submitAPCheckConfirm(int id);

	void resSubmitAPCheckConfirm(int id);
}

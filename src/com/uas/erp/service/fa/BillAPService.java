package com.uas.erp.service.fa;

import net.sf.json.JSONObject;

public interface BillAPService {
	void saveBillAP(String formStore, String param, String caller);

	void updateBillAPById(String formStore, String param, String caller);

	void deleteBillAP(int bap_id, String caller);

	void printBillAP(int bap_id, String caller);

	String auditBillAP(int bap_id, String caller);

	void resAuditBillAP(int bap_id, String caller);

	void submitBillAP(int bap_id, String caller);

	void resSubmitBillAP(int bap_id, String caller);

	void nullifyBillAP(int bap_id, String caller);

	void getSend(String caller, String data);

	void updateInfo(int id, String text, String caller);
	
	JSONObject copyBillAP(int id, String caller);
}

package com.uas.erp.service.scm;

public interface PurchaseCloseService {
	void deletePurchaseClose(int pc_id, String caller);
	void auditPurchaseClose( int pc_id, String caller);
	void resAuditPurchaseClose(int pc_id, String caller);
	void submitPurchaseClose(int pc_id, String caller);
	void resSubmitPurchaseClose(int pc_id, String caller);
}

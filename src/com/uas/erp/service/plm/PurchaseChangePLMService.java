package com.uas.erp.service.plm;

public interface PurchaseChangePLMService {
	void savePurchaseChange(String formStore, String gridStore, String caller);
	void updatePurchaseChangeById(String formStore, String gridStore, String caller);
	void deletePurchaseChange(int pc_id, String caller);
	void printPurchaseChange(int pc_id, String caller);
	void auditPurchaseChange(int pc_id, String caller);
	void resAuditPurchaseChange(int pc_id, String caller);
	void submitPurchaseChange(int pc_id, String caller);
	void resSubmitPurchaseChange(int pc_id, String caller);
	String turnPurchase(int pc_id, String caller);
}

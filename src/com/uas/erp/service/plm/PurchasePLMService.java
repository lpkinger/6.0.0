package com.uas.erp.service.plm;

public interface PurchasePLMService {
	void savePurchase(String formStore, String gridStore, String caller);

	void updatePurchaseById(String formStore, String gridStore, String caller);

	void deletePurchase(int pu_id, String caller);

	void printPurchase(int pu_id, String caller);

	void auditPurchase(int pu_id, String caller);

	void resAuditPurchase(int pu_id, String caller);

	void submitPurchase(int pu_id, String caller);

	void resSubmitPurchase(int pu_id, String caller);

	void endPurchase(int pu_id, String caller);

	void resEndPurchase(int pu_id, String caller);
}

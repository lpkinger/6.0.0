package com.uas.erp.service.scm;

public interface PurchaseKindService {
	void savePurchaseKind(String formStore, String caller);
	void updatePurchaseKindById(String formStore, String caller);
	void deletePurchaseKind(int pk_id, String caller);
}

package com.uas.erp.service.scm;

public interface PurchaseForecastService {
	void savePurchaseForecast(String formStore, String gridStore, String caller);
	void updatePurchaseForecastById(String formStore, String gridStore, String caller);
	void deletePurchaseForecast(int pf_id, String caller);
	void printPurchaseForecast(int pf_id, String caller);
	void auditPurchaseForecast(int pf_id, String caller);
	void resAuditPurchaseForecast(int pf_id, String caller);
	void submitPurchaseForecast(int pf_id, String caller);
	void resSubmitPurchaseForecast(int pf_id, String caller);
	void getVendor(int[] id);
	void confirm(int[] id);
}

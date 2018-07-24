package com.uas.erp.service.pm;

public interface AppMouldService {
	void saveAppMould(String formStore, String gridStore, String caller);

	void updateAppMouldById(String formStore, String gridStore, String caller);

	void deleteAppMould(int app_id, String caller);

	void printAppMould(int app_id, String caller);

	void auditAppMould(int app_id, String caller);

	void resAuditAppMould(int app_id, String caller);

	void submitAppMould(int app_id, String caller);

	void resSubmitAppMould(int app_id, String caller);

	String turnPriceMould(String data, String caller);

	String turnMouldSale(int app_id);

	void updateOffer(String data);

	void createARBill(int app_id);
}

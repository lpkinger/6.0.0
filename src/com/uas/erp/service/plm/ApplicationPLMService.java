package com.uas.erp.service.plm;

public interface ApplicationPLMService {
	void saveApplication(String formStore, String gridStore, String caller);

	void updateApplicationById(String formStore, String gridStore, String caller);

	void deleteApplication(int ap_id, String caller);

	void printApplication(int ap_id, String caller);

	void auditApplication(int ap_id, String caller);

	void resAuditApplication(int ap_id, String caller);

	void submitApplication(int ap_id, String caller);

	void resSubmitApplication(int ap_id, String caller);

	int turnPurchase(int ap_id, String caller);

	void getVendor(int[] id);

	String[] postApplication(int[] id, int ma_id_f, int ma_id_t);
}

package com.uas.erp.service.hr;


public interface KBIChangeManService {
	void saveKBIChangeMan(String formStore, String caller);
	void deleteKBIChangeMan(int kc_id, String caller);
	void updateKBIChangeManById(String formStore, String caller);
	void submitKBIChangeMan(int kc_id, String caller);
	void resSubmitKBIChangeMan(int kc_id, String caller);
	void auditKBIChangeMan(int kc_id, String caller);
}

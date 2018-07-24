package com.uas.erp.service.hr;


public interface KBIAssessService {
	void saveKBIAssess(String formStore, String gridStore, String caller);
	void deleteKBIAssess(int ka_id, String caller);
	void updateKBIAssessById(String formStore,String gridStore, String caller);
	void submitKBIAssess(int ka_id, String caller);
	void resSubmitKBIAssess(int ka_id, String caller);
	void auditKBIAssess(int ka_id, String caller);
	void resAuditKBIAssess(int ka_id, String caller);
	void turnKBIBill(String caller,String data);
	int autoSave(String caller, String ka_detp);
}

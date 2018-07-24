package com.uas.erp.service.crm;



public interface AssistRequireService {
	void saveAssistRequire(String formStore, String gridStore,String caller);
	void deleteAssistRequire(int ar_id,String caller);
	void updateAssistRequireById(String formStore,String gridStore,String caller);
	void submitAssistRequire(int ar_id,String caller);
	void resSubmitAssistRequire(int ar_id,String caller);
	void auditAssistRequire(int ar_id,String caller);
	void resAuditAssistRequire(int ar_id,String caller);
}

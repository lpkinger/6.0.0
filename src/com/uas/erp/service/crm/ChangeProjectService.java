package com.uas.erp.service.crm;



public interface ChangeProjectService {
	void saveChangeProject(String formStore,String caller);
	void deleteChangeProject(int cp_id,String caller);
	void updateChangeProject(String formStore,String caller);
	void auditChangeProject(int cp_id,String caller);
	void resAuditChangeProject(int cp_id,String caller);
	void submitChangeProject(int cp_id,String caller);
	void resSubmitChangeProject(int cp_id,String caller);
}

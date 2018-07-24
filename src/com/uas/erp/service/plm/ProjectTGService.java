package com.uas.erp.service.plm;

public interface ProjectTGService {

	void saveProjectTG(String formStore, String gridStore,String caller);
	String copy(int prj_id);
	void auditProjectTG(int prj_id,String caller);
	void resAuditProjectTG(int prj_id,String caller);
	void updateProjectTG(String caller, String formStore, String gridStore);
	void deleteProjectTG(int prj_id, String caller);
}

package com.uas.erp.service.plm;

public interface ProjectTaskChangeService {
	void saveProjectTaskChange(String formStore, String gridStore);
	void deleteProjectTaskChange(int id);
	void updateProjectTaskChange(String  formStore, String  param);
	void auditProjectTaskChange(int id);
	void submitProjectTaskChange(int id);
	void resSubmitProjectTaskChange(int id);
	void resAuditProjectTaskChange( int id);
}

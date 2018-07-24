package com.uas.erp.service.plm;

public interface ProjectChangeService {
	void saveProjectChange(String formStore, String caller);

	void deleteProjectChange(int id, String caller);

	void updateProjectChange(String formStore, String caller);

	void auditProjectChange(int id, String caller);

	void submitProjectChange(int id, String caller);

	void resSubmitProjectChange(int id, String caller);

	void resAuditProjectChange(int id, String caller);
}

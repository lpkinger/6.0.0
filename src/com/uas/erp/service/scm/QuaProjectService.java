package com.uas.erp.service.scm;

public interface QuaProjectService {
	void saveQuaProject(String caller, String formStore, String gridStore);

	void updateQuaProjectById(String caller, String formStore, String gridStore);

	void deleteQuaProject(String caller, int pr_id);

	void printQuaProject(String caller, int pr_id);

	void auditQuaProject(String caller, int pr_id);

	void resAuditQuaProject(String caller, int pr_id);

	void submitQuaProject(String caller, int pr_id);

	void resSubmitQuaProject(String caller, int pr_id);
}

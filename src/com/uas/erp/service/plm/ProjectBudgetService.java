package com.uas.erp.service.plm;

public interface ProjectBudgetService {
	void saveProjectBudget(String formStore, String gridStore, String caller);

	void deleteProjectBudget(int id, String caller);

	void updateProjectBudget(String formStore, String param, String caller);

	void auditProjectBudget(int id, String caller);

	void submitProjectBudget(int id, String caller);

	void resSubmitProjectBudget(int id, String caller);

	void resAuditProjectBudget(int id, String caller);

	String getData(int id);
}

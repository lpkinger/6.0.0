package com.uas.erp.service.plm;

public interface ProjectCostService {
	void saveProjectCost(String formStore, String caller);

	void updateProjectCostById(String formStore, String caller);

	void deleteProjectCost(int pc_id, String caller);

	void auditProjectCost(int pc_id, String caller);

	void resAuditProjectCost(int pc_id, String caller);

	void submitProjectCost(int pc_id, String caller);

	void resSubmitProjectCost(int pc_id, String caller);

	void startAccount();

	void getSharedCosts(Integer param);

	void sharedCount(Integer param);

}

package com.uas.erp.service.plm;

public interface ProjectCostCloseService {
	void saveProjectCostClose(String formStore, String gridStore, String caller);

	void deleteProjectCostClose(int id, String caller);

	void updateProjectCostClose(String formStore, String param, String caller);

	void submitProjectCostClose(int id, String caller);

	void resSubmitProjectCostClose(int id, String caller);

	void auditProjectCostClose(int id, String caller);

	void resAuditProjectCostClose(int id, String caller);

	int createCostVoucher(int id, String caller);

	void cancelCostVoucher(int id, String caller);

	void catchProjectCost(String caller, String formStore);

	void cleanProjectCost(String caller, String formStore);
}

package com.uas.erp.service.fa;

public interface ReturnPlanService {
	void saveReturnPlan(String formStore, String gridStore, String caller);

	void updateReturnPlanById(String formStore, String gridStore, String caller);

	void deleteReturnPlan(int ccr_id, String caller);

	void submitReturnPlan(int ccr_id, String caller);

	void resSubmitReturnPlan(int ccr_id, String caller);

	void auditReturnPlan(int ccr_id, String caller);

	void resAuditReturnPlan(int ccr_id, String caller);

}

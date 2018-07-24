package com.uas.erp.service.crm;

public interface ExpandPlanService {
	void saveExpandPlan(String formStore, String gridStore, String caller);

	void deleteExpandPlan(int ep_id, String caller);

	void updateExpandPlanById(String formStore, String gridStore, String caller);

	void submitExpandPlan(int ep_id, String caller);

	void resSubmitExpandPlan(int ep_id, String caller);

	void auditExpandPlan(int ep_id, String caller);

	void resAuditExpandPlan(int ep_id, String caller);
}

package com.uas.erp.service.crm;



public interface ResearchPlanService {
	void saveResearchPlan(String formStore,String caller);
	void deleteResearchPlan(int rp_id,String caller);
	void updateResearchPlan(String formStore,String caller);
	void auditResearchPlan(int rp_id,String caller);
	void resAuditResearchPlan(int rp_id,String caller);
	void submitResearchPlan(int rp_id,String caller);
	void resSubmitResearchPlan(int rp_id,String caller);
}

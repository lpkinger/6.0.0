package com.uas.erp.service.crm;



public interface PlanDisingService {

	void savePlanDising(String formStore, String param,String caller);
	void deletePlanDising(int id,String caller);
	void updatePlanDising(String formStore, String param, 
			String caller);
	void resSubmitPlanDising(int id,String caller);
	void auditPlanDising(int id,String caller);
	void resAuditPlanDising(int id,String caller);
	void submitPlanDising(int id,String caller);

}

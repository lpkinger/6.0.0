package com.uas.erp.service.crm;



public interface MProjectPlanService {
	void saveMProjectPlan(String formStore,String gridStore,String caller);
	void deleteMProjectPlan(int prjplan_id,String caller);
	void updateMProjectPlan(String formStore,String gridStore,String caller);
	void submitMProjectPlan(int prjplan_id,String caller);
	void resSubmitMProjectPlan(int prjplan_id,String caller);
	void auditMProjectPlan(int prjplan_id,String caller);
	void resAuditMProjectPlan(int prjplan_id,String caller);
	void turnTask(int prjplan_id,String caller);
	void updateTask(String gridStore,String caller);
}

package com.uas.erp.service.crm;



public interface CallPlanService {
	void saveCallPlan(String formStore,String caller);
	void deleteCallPlan(int cp_id,String caller);
	void updateCallPlan(String formStore,String caller);
}

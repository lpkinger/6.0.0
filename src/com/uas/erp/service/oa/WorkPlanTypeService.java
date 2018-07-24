package com.uas.erp.service.oa;


public interface WorkPlanTypeService {
	
	void saveWorkPlanType(String formStore,String caller);
	void updateWorkPlanType(String formStore, String  caller);
	void deleteWorkPlanType(int wpt_id, String  caller);

}

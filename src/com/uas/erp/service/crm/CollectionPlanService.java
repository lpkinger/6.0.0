package com.uas.erp.service.crm;



public interface CollectionPlanService {
	void saveCollectionPlan(String formStore,String caller);
	void deleteCollectionPlan(int cp_id,String caller);
	void updateCollectionPlan(String formStore,String caller);
}

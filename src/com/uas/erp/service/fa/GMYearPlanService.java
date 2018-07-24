package com.uas.erp.service.fa;

public interface GMYearPlanService {
	void saveGMYearPlan(String formStore, String gridStore, String caller);

	void updateGMYearPlanById(String formStore, String gridStore, String caller);

	void deleteGMYearPlan(int gmp_id, String caller);

}

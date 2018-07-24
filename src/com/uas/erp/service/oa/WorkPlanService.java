package com.uas.erp.service.oa;

import java.util.List;

import com.uas.erp.model.WorkPlan;
import com.uas.erp.model.WorkPlanDetail;

public interface WorkPlanService {
	void saveWorkPlan(String formStore, String  caller);
	void updateWorkPlan(String formStore, String  caller);
	void deleteWorkPlan(int wp_id, String  caller);
	WorkPlan getWorkPlan(int wp_id, String  caller);
	List<WorkPlanDetail> getWorkPlanDetailList(int wpd_wpid, String  caller);
	void deleteWorkPlanDetail(int wpd_id, String  caller);
	void updateWorkPlanDetail(String formStore, String caller);
	void saveWorkPlanDetail(String formStore, String  caller);
	WorkPlan queryWorkPlan(String title, String  caller);
}

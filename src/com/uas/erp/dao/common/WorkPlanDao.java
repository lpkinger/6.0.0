package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.WorkPlan;
import com.uas.erp.model.WorkPlanDetail;

public interface WorkPlanDao {
	
	WorkPlan getWorkPlanById(int wp_id);
	List<WorkPlanDetail> getWorkPlanDetailList(int wpd_wpid);
	WorkPlan getWorkPlanByTitle(String title);

}

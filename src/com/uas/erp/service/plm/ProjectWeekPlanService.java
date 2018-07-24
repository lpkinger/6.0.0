package com.uas.erp.service.plm;

import java.util.Map;

public interface ProjectWeekPlanService {

	Map<String,Object> getProjectList();
	
	void updateProject(String formStore);
	
}

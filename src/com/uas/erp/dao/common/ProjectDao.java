package com.uas.erp.dao.common;

import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProjectPlan;
public interface ProjectDao {
	JSONTree getJSONResource(String condition);
	@Deprecated
	ProjectPlan getProjectPlanByCode(String code);
	String TurnProjectReview(int id);
}

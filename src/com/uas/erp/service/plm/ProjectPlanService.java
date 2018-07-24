package com.uas.erp.service.plm;

import com.uas.erp.model.JSONTree;
import com.uas.erp.model.ProjectPlan;

public interface ProjectPlanService {
	void saveProjectPlan(String formStore, String param, String param2, String caller);

	void updateProjectPlan(String formStore, String caller);

	void deleteProjectPlan(int id, String caller);

	JSONTree getJSONResource(String condition);

	ProjectPlan getProjectPlanByCode(String code);

	void insert(String formStore, String caller);

	void submitProjectPlan(int id, String caller);

	void resSubmitProjectPlan(int id, String caller);

	void auditProjectPlan(int id, String caller);

	void resAuditProjectPlan(int id, String caller);

	String TurnProjectreview(int id, String caller);
}

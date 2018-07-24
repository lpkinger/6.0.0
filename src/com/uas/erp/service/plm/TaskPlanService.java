package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

public interface TaskPlanService {

	void saveTaskPlan(String formStore, String param1, String param2);

	void updateTaskPlanById(String formStore, String param1, String param2);

	void deleteTaskPlan(int id);

	void submitTaskPlan(int id);

	void resSubmitTaskPlan(int id);

	void auditTaskPlan(int id);

	void resAuditTaskPlan(int id);

	Object[] getWeek();

}

package com.uas.erp.service.fs;

import java.util.List;
import java.util.Map;

public interface LoadedPlanService {
	
	List<Map<String, Object>> getLoadedPlans(String pCaller, int pid, String type);
	
	void saveLoadedPlan(String formStore, String param2, String caller);

	void updateLoadedPlan(String formStore, String caller);

	//void deleteLoadedPlan(int id, String caller);

	void submitLoadedPlan(int id, String caller);

	void resSubmitLoadedPlan(int id, String caller);

	void auditLoadedPlan(int id, String caller);

	void resAuditLoadedPlan(int id, String caller);

}

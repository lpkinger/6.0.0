package com.uas.erp.service.plm;

import java.util.Map;


public interface ProjectRequestService {
	void saveProjectRequest(String caller,String formStore,String params1, String params2, String params3);
	void updateProjectRequest(String caller,String formStore,String params1,String params2,String params3,String params4);
	void deleteProjectRequest(String caller,int id);
	void auditProjectRequest(int id,String caller);
	void resAuditProjectRequest(String caller, int id);
	void submitProjectRequest(String caller,int id);
	void resSubmitProjectRequest(String caller,int id);
	void planMainTask(int id);
	Map<String,Object> getProjectPhase(String productType);
	int getIdByCode(String formCondition);
	void turnProject(String id);
	boolean isProjectSobHaveData(String id,String caller);
	boolean isProjectTaskHaveData(String id,String caller);
	int setMainProjectRule(String maincode);
}

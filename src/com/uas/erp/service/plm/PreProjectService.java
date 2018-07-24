package com.uas.erp.service.plm;

import com.uas.erp.model.Employee;

public interface PreProjectService {
	
	void updatePreProjectById(String formStore, String gridStore, String caller);

	void deletePreProject(int pp_id, String caller);

	void auditPreProject(int pp_id, String caller);

	void resAuditPreProject(int pp_id, String caller);

	void submitPreProject(int pp_id, String caller);

	void resSubmitPreProject(int pp_id, String caller);
	
	String turnProject(int pp_id, String caller,String title,Employee employee);
	
	Object getID(String formCondition);

	void changeResponsible(String caller,int id,String newman,Employee employee);
}

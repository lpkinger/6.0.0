package com.uas.erp.service.hr;

import java.util.List;

import net.sf.json.JSONObject;

public interface DepartmentnewService {

	void saveDepartment(String formStore, String  caller);

	void updateDepartmentById(String formStore, String caller);

	void deleteDepartment(int id, String  caller);
	
	void resAuditDepartment(int id, String  caller);
	
	List<JSONObject> getDepartments();

	void auditDepartment(int id, String caller);
}

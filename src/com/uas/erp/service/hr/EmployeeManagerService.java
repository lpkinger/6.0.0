package com.uas.erp.service.hr;

import java.util.List;

public interface EmployeeManagerService {

	void saveEmployee(String formStore, String jobItems, String  caller,Boolean JobOrgNoRelation);

	void updateEmployeeById(String formStore, String jobItems, String extra, String  caller,Boolean JobOrgNoRelation);

	void deleteEmployee(int or_id, String  caller);

	void deleteExtraJob(int empId, int jobId, String  caller);

	void vastTurnOver(String caller,  Integer[] id);

	void vastTurnfullmemb(String caller, Integer[] id);

	void turnFullmemb(String caller,  int[] id);

	void updatePosition(String param, String  caller);

	void turnCaree(String  caller, int[] id);

	String[] printUnpackApply(int id, String  caller, String reportName, String condition);
	
	List<String> searchEmployeesByKey(String keyword);
	
	String vastTurnfullmemb(String caller, String data);
	
	String vastLZTurnZS(String caller, String data);
	
	String vastTurnContract(String caller, String data);
	
	void postEmployee(String datas, String to);

	String checkEmcode(String emcode, String emname);

}

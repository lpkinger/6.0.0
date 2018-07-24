package com.uas.erp.service.hr;


public interface HrEmployeeService {
	void saveEmployee(String formStore, String gridStore, String caller);

	void updateEmployeeById(String formStore, String gridStore, String caller);

	void deleteEmployee(int em_id, String caller);

	void printEmployee(int em_id, String caller);

	void auditEmployee(int em_id, String caller);

	void resAuditEmployee(int em_id, String caller);

	void submitEmployee(int em_id, String caller);

	void resSubmitEmployee(int em_id, String caller);
}

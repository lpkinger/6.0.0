package com.uas.erp.service.hr;

public interface DepartmentCrtService {
	void saveDepartmentCrt(String formStore, String gridStore, String caller);
	void deleteDepartmentCrt(int id, String caller);
	void updateDepartmentCrt(String formStore, String gridStore, String caller);
	void submitDepartmentCrt(int id, String caller);
	void resSubmitDepartmentCrt(int id, String caller);
	void auditDepartmentCrt(int id, String caller);
	void resAuditDepartmentCrt(int id, String caller);
}

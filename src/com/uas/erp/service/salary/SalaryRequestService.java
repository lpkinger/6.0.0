package com.uas.erp.service.salary;

public interface SalaryRequestService {

	void saveRequire(String formStore, String caller);

	void updateRequireById(String formStore, String caller);

	void deleteRequire(int id, String caller);

	void submitRequire(int id, String caller);

	void resSubmitRequire(int id, String caller);

	void auditRequire(int id, String caller);

	void resAuditRequire(int id, String caller);

}

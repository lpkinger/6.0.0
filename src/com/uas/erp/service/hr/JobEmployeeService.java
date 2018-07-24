package com.uas.erp.service.hr;

import org.springframework.stereotype.Service;
@Service
public interface JobEmployeeService {
	void saveJobEmployee(String formStore, String gridStore, String caller);
	void updateJobEmployeeById(String formStore, String gridStore, String caller);
	void deleteJobEmployee(int je_id, String caller);
	void printJobEmployee(int je_id, String caller);
	void auditJobEmployee(int je_id, String caller);
	void resAuditJobEmployee(int je_id, String caller);
	void submitJobEmployee(int je_id, String caller);
	void resSubmitJobEmployee(int je_id, String caller);
	

}

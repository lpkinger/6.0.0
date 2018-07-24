package com.uas.erp.service.hr;


public interface WorkovertimeService {
	
	void saveWorkovertime(String formStore, String gridStore, String caller);

	void updateWorkovertimeById(String formStore, String gridStore,
			String  caller);

	void deleteWorkovertime(int wo_id, String  caller);

	void auditWorkovertime(int wo_id, String  caller);

	void resAuditWorkovertime(int wo_id, String  caller);

	void submitWorkovertime(int wo_id, String  caller);

	void resSubmitWorkovertime(int wo_id, String  caller);

	void syncDB(String caller, int id);

	void confirmWorkovertime(int id, String  caller);
}

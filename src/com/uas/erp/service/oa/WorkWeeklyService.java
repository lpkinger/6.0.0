package com.uas.erp.service.oa;


public interface WorkWeeklyService {
	void saveWorkWeekly(String formStore, String  caller);
	void updateWorkWeekly(String formStore, String  caller);
	void deleteWorkWeekly(int bd_id, String  caller);
	void submitWorkWeekly(int ww_id, String caller);
	void resSubmitWorkWeekly(int ww_id, String caller);
	void auditWorkWeekly(int ww_id, String caller);
	void resAuditWorkWeekly(int ww_id, String caller);
	void catchWorkContentWeekly(int ww_id, String caller);
	void workWeeklyLimit(String ww_starttime);
}

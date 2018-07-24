package com.uas.erp.service.oa;


public interface WorkMonthlyService {
	void saveWorkMonthly(String formStore, String  caller);
	void updateWorkMonthly(String formStore, String  caller);
	void deleteWorkMonthly(int bd_id, String  caller);
	void submitWorkMonthly(int wm_id, String caller);
	void resSubmitWorkMonthly(int wm_id, String caller);
	void auditWorkMonthly(int wm_id, String caller);
	void resAuditWorkMonthly(int wm_id, String caller);
	void catchWorkContentMonthly(int wm_id, String caller);
	void workWeeklyLimit(String wm_month);
}

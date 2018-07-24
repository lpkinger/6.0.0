package com.uas.erp.service.oa;


public interface WorkDailyService {
	void workDailyLimit(String wd_date);
	void saveWorkDaily(String formStore, String  param, String caller);
	void updateWorkDaily(String formStore, String gridStore, String  caller);
	void deleteWorkDaily(int bd_id, String  caller);
	void submitWorkDaily(int wd_id, String caller);
	void resSubmitWorkDaily(int wd_id, String caller);
	void auditWorkDaily(int wd_id, String caller);
	void resAuditWorkDaily(int wd_id, String caller);
	void catchWorkContent(int wd_id, String caller);
}

package com.uas.erp.service.hr;


public interface ReandpunishService {
	
	void saveReandpunish(String formStore, String  caller);
	
	void updateReandpunishById(String formStore, String  caller);
	
	void deleteReandpunish(int or_id, String  caller);
	
	void auditReandpunish(int re_id, String  caller);
	
	void resAuditReandpunish(int re_id, String  caller);
	
	void submitReandpunish(int re_id, String  caller);
	
	void resSubmitReandpunish(int re_id, String  caller);
}

package com.uas.erp.service.hr;


public interface RecruitactivityService {
	
	void saveRecruitactivity(String formStore, String gridstore,String caller);
	
	void updateRecruitactivityById(String formStore,String gridstore, String caller);
	
	void deleteRecruitactivity(int or_id, String caller);
	
	void auditRecruitactivity(int re_id, String caller);
	
	void resAuditRecruitactivity(int re_id, String caller);
	
	void submitRecruitactivity(int re_id, String caller);
	
	void resSubmitRecruitactivity(int re_id, String caller);
}

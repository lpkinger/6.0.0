package com.uas.erp.service.hr;


public interface RecruitplanService {
	
	void saveRecruitplan(String formStore, String gridStore, String  caller);
	
	void updateRecruitplanById(String formStore, String gridStore, String  caller);
	
	void deleteRecruitplan(int rp_id, String  caller);
	
	void auditRecruitplan(int rp_id, String  caller);
	
	void resAuditRecruitplan(int rp_id, String  caller);
	
	void submitRecruitplan(int rp_id, String  caller);
	
	void resSubmitRecruitplan(int rp_id, String  caller);

}

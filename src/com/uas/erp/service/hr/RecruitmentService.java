package com.uas.erp.service.hr;


public interface RecruitmentService {
	
	void saveRecruitment(String formStore, String gridStore,String  caller);
	
	void updateRecruitmentById(String formStore, String gridStore,String  caller);
	
	void deleteRecruitment(int re_id, String  caller);
	
	void auditRecruitment(int re_id, String  caller);
	
	void resAuditRecruitment(int re_id, String  caller);
	
	void submitRecruitment(int re_id, String  caller);
	
	void resSubmitRecruitment(int re_id, String  caller);
	
	String turnRecruitplan( String formdata, String griddata,String caller);
}
 
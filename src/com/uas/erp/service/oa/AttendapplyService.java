package com.uas.erp.service.oa;


public interface AttendapplyService {
	
	void saveAttendapply(String formStore, String caller);
	
	void updateAttendapplyById(String formStore, String caller);
	
	void deleteAttendapply(int aa_id, String caller);
	
	void auditAttendapply(int aa_id, String caller);
	
	void resAuditAttendapply(int aa_id, String caller);
	
	void submitAttendapply(int aa_id, String caller);
	
	void resSubmitAttendapply(int aa_id, String caller);
	
}

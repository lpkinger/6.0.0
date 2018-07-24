package com.uas.erp.service.hr;


public interface SpeAttendanceService {

	void saveSpeAttendance(String formStore, String caller);

	void updateSpeAttendance(String formStore, String caller);

	void deleteSpeAttendance(int id, String caller);

	void auditSpeAttendance(int id, String caller);

	void resAuditSpeAttendance(int id, String caller);

	void submitSpeAttendance(int id, String caller);

	void resSubmitSpeAttendance(int id, String caller);

	void confirmSpeAttendance(int id, String caller);
	
	void endSpeAttendance(int id, String caller);
	
	void resEndSpeAttendance(int id, String caller);

}

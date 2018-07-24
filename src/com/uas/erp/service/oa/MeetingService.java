package com.uas.erp.service.oa;


public interface MeetingService {
	void saveMeeting(String formStore, String gridStore, String  caller);
	void updateMeetingById(String formStore, String gridStore, String  caller);
	void deleteMeeting(int me_id, String  caller);
	void deleteDetail(int md_id, String  caller);
	//void printMeeting(int me_id, String  caller);
	void auditMeeting(int me_id, String  caller);
	void resAuditMeeting(int me_id, String  caller);
	void submitMeeting(int me_id, String  caller);
	void resSubmitMeeting(int me_id, String  caller);
	void changeMeetingStatus(int me_id, String em_code);
}

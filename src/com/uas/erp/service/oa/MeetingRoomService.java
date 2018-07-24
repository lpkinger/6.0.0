package com.uas.erp.service.oa;


public interface MeetingRoomService {
	void saveMeetingRoom(String formStore,String gridStore, String  caller);
	void updateMeetingRoom(String formStore, String param, String  caller);
	void deleteMeetingRoom(int mr_id, String  caller);
	void auditMeetingRoom(int mr_id, String  caller);
	void resAuditMeetingRoom(int mr_id, String  caller);
	void submitMeetingRoom(int mr_id, String  caller);
	void resSubmitMeetingRoom(int mr_id, String  caller);
	String showapply(String gridStore, String condition);
}

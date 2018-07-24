package com.uas.erp.service.oa;

import java.text.ParseException;


public interface MeetingDocService {
	void saveMeetingDoc(String formStore, String  caller) throws ParseException;
	void updateMeetingDoc(String formStore, String  caller) throws ParseException;
	void deleteMeetingDoc(int md_id, String  caller);
	void auditMeetingDoc(int md_id, String  caller);
	void resAuditMeetingDoc(int md_id, String  caller);
	void submitMeetingDoc(int md_id, String  caller);
	void resSubmitMeetingDoc(int md_id, String  caller);
	void meetingSign(String data,String caller);
}

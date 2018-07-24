package com.uas.erp.service.oa;

import java.text.ParseException;

public interface StandMeetingService {

	void saveStandMeeting(String formStore, String caller) throws ParseException;

	void deleteStandMeeting(int id, String caller);

	void updateStandMeeting(String formStore, String caller) throws ParseException;

	String turnMeeting(String data, String caller);

	void banStandMeeting(int sm_id, String caller);

	void resBanStandMeeting(int sm_id, String caller);

	void submitStandMeeting(int id, String caller);

	void resSubmitStandMeeting(int id, String caller);

	void auditStandMeeting(int id, String caller);

	void resAuditStandMeeting(int id, String caller);

}

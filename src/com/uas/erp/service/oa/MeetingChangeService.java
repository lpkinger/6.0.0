package com.uas.erp.service.oa;

public interface MeetingChangeService {
	void saveMeetingChange(String formStore, String caller);

	void deleteMeetingChange(int mc_id, String caller);

	void updateMeetingChange(String formStore, String caller);

	void submitMeetingChange(int mc_id, String caller);

	void resSubmitMeetingChange(int mc_id, String caller);

	void auditMeetingChange(int mc_id, String caller);

	void resAuditMeetingChange(int mc_id, String caller);
}

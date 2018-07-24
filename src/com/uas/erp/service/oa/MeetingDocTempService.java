package com.uas.erp.service.oa;

public interface MeetingDocTempService {
	void saveMeetingDocTemp(String formStore, String caller);

	void deleteMeetingDocTemp(int mt_id, String caller);

	void updateMeetingDocTemp(String formStore, String caller);
}

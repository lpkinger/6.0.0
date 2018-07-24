package com.uas.erp.service.oa;


public interface SentCarReasonCodeService {
	void saveSentCarReasonCode(String formStore, String  caller);
	void updateSentCarReasonCode(String formStore, String  caller);
	void deleteSentCarReasonCode(int scr_id, String  caller);
}

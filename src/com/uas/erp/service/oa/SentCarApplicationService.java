package com.uas.erp.service.oa;


public interface SentCarApplicationService {
	void saveSentCarApplication(String formStore, String  caller);
	void updateSentCarApplication(String formStore, String  caller);
	void deleteSentCarApplication(int sca_id, String  caller);
	void auditSentCarApplication(int sca_id, String  caller);
	void resAuditSentCarApplication(int sca_id, String  caller);
	void submitSentCarApplication(int sca_id, String  caller);
	void resSubmitSentCarApplication(int sca_id, String  caller);
}

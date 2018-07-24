package com.uas.erp.service.scm;

public interface ReplaceRateChangeService {
	void saveReplaceRateChange(String formStore, String gridStore, String caller);

	void updateReplaceRateChangeById(String formStore, String gridStore, String caller);

	void deleteReplaceRateChange(int rc_id, String caller);

	void printReplaceRateChange(int rc_id, String caller);

	void auditReplaceRateChange(int rc_id, String caller);

	void resAuditReplaceRateChange(int rc_id, String caller);

	void submitReplaceRateChange(int rc_id, String caller);

	void resSubmitReplaceRateChange(int rc_id, String caller);

}

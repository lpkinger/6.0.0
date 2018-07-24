package com.uas.erp.service.scm;

public interface RenewApplyService {
	void saveRenewApply(String formStore, String gridStore, String caller);
	void updateRenewApplyById(String formStore, String gridStore, String caller);
	void deleteRenewApply(int ra_id, String caller);
	void printRenewApply(int ra_id, String caller);
	void auditRenewApply(int ra_id, String caller);
	void resAuditRenewApply(int ra_id, String caller);
	void submitRenewApply(int ra_id, String caller);
	void resSubmitRenewApply(int ra_id, String caller);
}

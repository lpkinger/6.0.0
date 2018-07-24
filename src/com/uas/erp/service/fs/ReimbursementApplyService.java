package com.uas.erp.service.fs;

public interface ReimbursementApplyService {
	void saveReimbursementApply(String formStore, String caller);

	void updateReimbursementApply(String formStore, String caller);

	void deleteReimbursementApply(int id, String caller);

	void submitReimbursementApply(int id, String caller);

	void resSubmitReimbursementApply(int id, String caller);

	void auditReimbursementApply(int id, String caller);

	void resAuditReimbursementApply(int id, String caller);

}

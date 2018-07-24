package com.uas.erp.service.pm;

public interface BadCodeService {
	void saveBadCode(String formStore, String caller);

	void updateBadCodeById(String formStore, String caller);

	void deleteBadCode(int bc_id, String caller);

	void auditBadCode(int bc_id, String caller);

	void resAuditBadCode(int bc_id, String caller);

	void submitBadCode(int bc_id, String caller);

	void resSubmitBadCode(int bc_id, String caller);
}

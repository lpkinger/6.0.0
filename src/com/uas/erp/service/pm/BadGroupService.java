package com.uas.erp.service.pm;

public interface BadGroupService {
	void saveBadGroup(String formStore, String caller);

	void updateBadGroupById(String formStore, String caller);

	void deleteBadGroup(int bg_id, String caller);

	void auditBadGroup(int bg_id, String caller);

	void resAuditBadGroup(int bg_id, String caller);

	void submitBadGroup(int bg_id, String caller);

	void resSubmitBadGroup(int bg_id, String caller);
}

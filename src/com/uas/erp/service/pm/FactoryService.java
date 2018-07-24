package com.uas.erp.service.pm;

public interface FactoryService {
	void saveFactory(String formStore, String caller);

	void updateFactoryById(String formStore, String caller);

	void deleteFactory(int fa_id, String caller);

	void auditFactory(int fa_id, String caller);

	void resAuditFactory(int fa_id, String caller);

	void submitFactory(int fa_id, String caller);

	void resSubmitFactory(int fa_id, String caller);
}

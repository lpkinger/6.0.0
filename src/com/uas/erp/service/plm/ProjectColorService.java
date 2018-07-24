package com.uas.erp.service.plm;

public interface ProjectColorService {
	void saveProjectColor(String formStore, String caller);

	void deleteProjectColor(int id, String caller);

	void updateProjectColor(String formStore, String caller);

	void auditProjectColor(String formStore, String caller);

	void submitProjectColor(int id, String caller);

	void resSubmitProjectColor(int id, String caller);

	void resAuditProjectColor(int id, String caller);
}

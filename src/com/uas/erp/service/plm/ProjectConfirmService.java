package com.uas.erp.service.plm;

public interface ProjectConfirmService {
	void saveProjectConfirm(String formStore, String caller);

	void deleteProjectConfirm(int id, String caller);

	void updateProjectConfirm(String formStore, String caller);

	void auditProjectConfirm(int id, String caller);

	void submitProjectConfirm(int id, String caller);

	void resSubmitProjectConfirm(int id, String caller);

	void resAuditProjectConfirm(int id, String caller);

}

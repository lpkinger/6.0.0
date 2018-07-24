package com.uas.erp.service.plm;

public interface ProjectCloseService {
	void saveProjectClose(String formStore, String caller);
	void updateProjectCloseById(String formStore, String caller);
	void deleteProjectClose(int id, String caller);
	void submitProjectClose(int id, String caller);
	void resSubmitProjectClose(int id, String caller);
	void auditProjectClose(int id, String caller);
	void resAuditProjectClose(int id, String caller);
}

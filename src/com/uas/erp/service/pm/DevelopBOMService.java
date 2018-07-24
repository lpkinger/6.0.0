package com.uas.erp.service.pm;

public interface DevelopBOMService {
	void saveDevelopBOM(String formStore, String gridStore, String caller);
	void updateDevelopBOMById(String formStore, String gridStore, String caller);
	void deleteDevelopBOM(int bo_id, String caller);
	void auditDevelopBOM(int bo_id, String caller);
	void resAuditDevelopBOM(int bo_id, String caller);
	void submitDevelopBOM(int bo_id, String caller);
	void resSubmitDevelopBOM(int bo_id, String caller);
}

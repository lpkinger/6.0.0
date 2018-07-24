package com.uas.erp.service.pm;

public interface BOMChangeService {
	void saveBOM(String formStore, String caller);
	void updateBOMById(String formStore, String caller);
	void deleteBOM(int bo_id, String caller);
	void auditBOM(int bo_id, String caller);
	void resAuditBOM(int bo_id, String caller);
	void submitBOM(int bo_id, String caller);
	void resSubmitBOM(int bo_id, String caller);
}

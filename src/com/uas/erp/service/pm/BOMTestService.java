package com.uas.erp.service.pm;

public interface BOMTestService {
	void saveBOMTest(String formStore, String gridStore, String caller);
	void updateBOMTestById(String formStore, String gridStore, String caller);
	void deleteBOMTest(int bo_id, String caller);
	void auditBOMTest(int bo_id, String caller);
	void resAuditBOMTest(int bo_id, String caller);
	void submitBOMTest(int bo_id, String caller);
	void resSubmitBOMTest(int bo_id, String caller);
}

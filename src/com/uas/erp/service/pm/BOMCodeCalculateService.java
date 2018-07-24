package com.uas.erp.service.pm;


public interface BOMCodeCalculateService {
	void saveBOMCodeCalculate(String formStore, String gridStore, String caller);
	void updateBOMCodeCalculateById(String formStore, String gridStore, String caller);
	void deleteBOMCodeCalculate(int bo_id, String caller);
	void auditBOMCodeCalculate(int bo_id, String caller);
	void resAuditBOMCodeCalculate(int bo_id, String caller);
	void submitBOMCodeCalculate(int bo_id, String caller);
	void resSubmitBOMCodeCalculate(int bo_id, String caller);
}

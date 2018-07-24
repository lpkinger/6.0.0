package com.uas.erp.service.pm;

public interface BOMSonSeqencingService {
	void saveBOMSonSeqencing(String formStore, String gridStore, String caller);
	void updateBOMSonSeqencingById(String formStore, String gridStore, String caller);
	void deleteBOMSonSeqencing(int bo_id, String caller);
	void auditBOMSonSeqencing(int bo_id, String caller);
	void resAuditBOMSonSeqencing(int bo_id, String caller);
	void submitBOMSonSeqencing(int bo_id, String caller);
	void resSubmitBOMSonSeqencing(int bo_id, String caller);
}

package com.uas.erp.service.scm;


public interface BigLineApplyService {
	void saveBigLineApply(String formStore, String gridStore, String caller);
	void deleteBigLineApply(int ba_id, String caller);
	void updateBigLineApplyById(String formStore, String gridStore,
			String caller);
	void submitBigLineApply(int ba_id, String caller);
	void resSubmitBigLineApply(int ba_id, String caller);
	void auditBigLineApply(int ba_id, String caller);
	void resAuditBigLineApply(int ba_id, String caller);
}

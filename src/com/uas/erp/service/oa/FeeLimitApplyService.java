package com.uas.erp.service.oa;


public interface FeeLimitApplyService {
	void saveFeeLimitApply(String formStore, String gridStore, String  caller);
	void deleteFeeLimitApply(int fa_id, String  caller);
	void updateFeeLimitApplyById(String formStore,String gridStore, String  caller);
	void submitFeeLimitApply(int fa_id, String  caller);
	void resSubmitFeeLimitApply(int fa_id, String  caller);
	void auditFeeLimitApply(int fa_id, String  caller);
	void resAuditFeeLimitApply(int fa_id, String  caller);
}

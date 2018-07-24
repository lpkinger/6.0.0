package com.uas.erp.service.oa;


public interface FeeLimitService {
	void saveFeeLimit(String formStore, String gridStore, String  caller);
	void deleteFeeLimit(int fl_id, String  caller);
	void updateFeeLimitById(String formStore,String gridStore, String  caller);
	void submitFeeLimit(int fl_id, String  caller);
	void resSubmitFeeLimit(int fl_id, String  caller);
	void auditFeeLimit(int fl_id, String  caller);
	void resAuditFeeLimit(int fl_id, String  caller);
}

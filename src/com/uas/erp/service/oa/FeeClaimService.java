package com.uas.erp.service.oa;


public interface FeeClaimService {
	void saveFeeClaim(String formStore,String gridStore, String  caller);
	void updateFeeClaimById(String formStore,String gridStore, String  caller);
	void deleteFeeClaim(int fc_id, String  caller);
	void auditFeeClaim(int fc_id, String  caller);
	void resAuditFeeClaim(int fc_id, String  caller);
	void submitFeeClaim(int fc_id, String  caller);
	void resSubmitFeeClaim(int fc_id, String  caller);
}

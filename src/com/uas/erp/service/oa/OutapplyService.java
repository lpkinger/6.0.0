package com.uas.erp.service.oa;


public interface OutapplyService {
	
	void saveOutapply(String formStore, String  caller);
	
	void updateOutapplyById(String formStore, String  caller);
	
	void deleteOutapply(int oa_id, String  caller);
	
	void auditOutapply(int oa_id, String  caller);
	
	void resAuditOutapply(int oa_id, String  caller);
	
	void submitOutapply(int oa_id, String  caller);
	
	void resSubmitOutapply(int oa_id, String  caller);
	
}

package com.uas.erp.service.oa;


public interface EvectionService {
	
	void saveEvection(String formStore, String  caller);
	
	void updateEvectionById(String formStore, String  caller);
	
	void deleteEvection(int ec_id, String  caller);
	
	void auditEvection(int ec_id, String  caller);
	
	void resAuditEvection(int ec_id, String  caller);
	
	void submitEvection(int ec_id, String  caller);
	
	void resSubmitEvection(int ec_id, String  caller);
	
}

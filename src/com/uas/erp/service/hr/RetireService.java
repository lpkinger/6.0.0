package com.uas.erp.service.hr;


public interface RetireService {
	
	void saveRetire(String formStore, String gridStore,String  caller);
	
	void updateRetireById(String formStore, String gridStore,String  caller);
	
	void deleteRetire(int re_id, String  caller);
	
	void auditRetire(int re_id, String  caller);
	
	void resAuditRetire(int re_id, String  caller);
	
	void submitRetire(int re_id, String  caller);
	
	void resSubmitRetire(int re_id, String  caller);
}

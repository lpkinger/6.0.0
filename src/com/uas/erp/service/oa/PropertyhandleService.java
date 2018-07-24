package com.uas.erp.service.oa;


public interface PropertyhandleService {
	
	void savePropertyhandle(String formStore, String gridStore,String  caller);
	
	void updatePropertyhandleById(String formStore, String gridStore,String  caller);
	
	void deletePropertyhandle(int ph_id, String  caller);
	
	void auditPropertyhandle(int ph_id, String  caller);
	
	void resAuditPropertyhandle(int ph_id, String  caller);
	
	void submitPropertyhandle(int ph_id, String  caller);
	
	void resSubmitPropertyhandle(int ph_id, String  caller);
	
	
}
 
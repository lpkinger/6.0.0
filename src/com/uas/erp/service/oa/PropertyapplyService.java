package com.uas.erp.service.oa;


public interface PropertyapplyService {
	
	void savePropertyapply(String formStore, String gridStore,String  caller);
	
	void updatePropertyapplyById(String formStore, String gridStore,String  caller);
	
	void deletePropertyapply(int pa_id, String  caller);
	
	void auditPropertyapply(int pa_id, String  caller);
	
	void resAuditPropertyapply(int pa_id, String  caller);
	
	void submitPropertyapply(int pa_id, String  caller);
	
	void resSubmitPropertyapply(int pa_id, String  caller);
	
	void getProperty(int id,String  caller,String param);
	
	void ReturnProperty(int id,String  caller,String param);
	
}
 
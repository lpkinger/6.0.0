package com.uas.erp.service.scm;

public interface ChangeService {
	void saveChange(String formStore, String gridstore,String caller);
	
	void updateChangeById(String formStore,String gridstore,String caller);
	
	void deleteChange(int cs_id, String caller);
	
	void auditChange(int cs_id,String caller);
	
	void resAuditChange(int cs_id,String caller);
	
	void submitChange(int cs_id, String caller);
	
	void resSubmitChange(int cs_id,String caller);
	
}

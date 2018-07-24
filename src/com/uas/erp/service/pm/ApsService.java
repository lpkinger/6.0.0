package com.uas.erp.service.pm;


public interface ApsService {
	
	void saveAps(String formStore, String gridStore,String caller);
	
	void updateApsById(String formStore, String gridStore,String caller);
	
	void deleteAps(int am_id,String caller);
	
	void auditAps(int am_id,String caller);
	
	void resAuditAps(int am_id,String caller);
	
	void submitAps(int am_id,String caller);
	
	void resSubmitAps(int am_id,String caller);
}

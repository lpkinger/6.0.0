package com.uas.erp.service.pm;


public interface MakeScrapmakeService {
	
	void saveMakeScrapmake(String formStore, String gridStore, String caller);
	void updateMakeScrapmakeById(String formStore, String gridStore, String caller);
	void deleteMakeScrapmake(int ms_id, String caller);
	void auditMakeScrapmake(int ms_id, String caller);
	void resAuditMakeScrapmake(int ms_id, String caller);
	void submitMakeScrapmake(int ms_id, String caller);
	void resSubmitMakeScrapmake(int ms_id, String caller);
}

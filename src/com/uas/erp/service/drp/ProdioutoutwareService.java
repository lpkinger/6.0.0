package com.uas.erp.service.drp;



public interface ProdioutoutwareService {
	
	void saveProdioutoutware(String formStore, String gridStore,  String caller);
	
	void updateProdioutoutwareById(String formStore, String gridStore,  String caller);
	
	void deleteProdioutoutware(int pi_id,  String caller);
	
	void auditProdioutoutware(int pi_id,  String caller);
	
	void resAuditProdioutoutware(int pi_id,  String caller);
	
	void submitProdioutoutware(int pi_id,  String caller);
	
	void resSubmitProdioutoutware(int pi_id,  String caller);
	
}

package com.uas.erp.service.drp;



public interface ProdioutinwareService {
	
	void saveProdioutinware(String formStore, String gridStore,  String caller);
	
	void updateProdioutinwareById(String formStore, String gridStore,  String caller);
	
	void deleteProdioutinware(int pi_id,  String caller);
	
	void auditProdioutinware(int pi_id,  String caller);
	
	void resAuditProdioutinware(int pi_id,  String caller);
	
	void submitProdioutinware(int pi_id,  String caller);
	
	void resSubmitProdioutinware(int pi_id,  String caller);
	
}

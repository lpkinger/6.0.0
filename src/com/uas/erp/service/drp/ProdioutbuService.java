package com.uas.erp.service.drp;



public interface ProdioutbuService {
	
	void saveProdioutbu(String formStore, String gridStore,  String caller);
	
	void updateProdioutbuById(String formStore, String gridStore,  String caller);
	
	void deleteProdioutbu(int pi_id,  String caller);
	
	void auditProdioutbu(int pi_id,  String caller);
	
	void resAuditProdioutbu(int pi_id,  String caller);
	
	void submitProdioutbu(int pi_id,  String caller);
	
	void resSubmitProdioutbu(int pi_id,  String caller);
	
}

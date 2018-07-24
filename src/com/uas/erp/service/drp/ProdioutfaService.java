package com.uas.erp.service.drp;



public interface ProdioutfaService {
	
	void saveProdioutfa(String formStore, String gridStore,  String caller);
	
	void updateProdioutfaById(String formStore, String gridStore,  String caller);
	
	void deleteProdioutfa(int pi_id,  String caller);
	
	void auditProdioutfa(int pi_id,  String caller);
	
	void resAuditProdioutfa(int pi_id,  String caller);
	
	void submitProdioutfa(int pi_id,  String caller);
	
	void resSubmitProdioutfa(int pi_id,  String caller);
	
}

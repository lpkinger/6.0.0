package com.uas.erp.service.drp;



public interface ProdioutlinService {
	
	void saveProdioutlin(String formStore, String gridStore,  String caller);
	
	void updateProdioutlinById(String formStore, String gridStore,  String caller);
	
	void deleteProdioutlin(int pi_id,  String caller);
	
	void auditProdioutlin(int pi_id,  String caller);
	
	void resAuditProdioutlin(int pi_id,  String caller);
	
	void submitProdioutlin(int pi_id,  String caller);
	
	void resSubmitProdioutlin(int pi_id,  String caller);
	
}

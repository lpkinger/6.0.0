package com.uas.erp.service.drp;



public interface ProdiouttuiService {
	
	void saveProdiouttui(String formStore, String gridStore,  String caller);
	
	void updateProdiouttuiById(String formStore, String gridStore,  String caller);
	
	void deleteProdiouttui(int pi_id,  String caller);
	
	void auditProdiouttui(int pi_id,  String caller);
	
	void resAuditProdiouttui(int pi_id,  String caller);
	
	void submitProdiouttui(int pi_id,  String caller);
	
	void resSubmitProdiouttui(int pi_id,  String caller);
	
}

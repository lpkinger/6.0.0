package com.uas.erp.service.hr;


public interface TurnfullmembService {
	
	void saveTurnfullmemb(String formStore, String gridStore,String caller);
	
	void updateTurnfullmembById(String formStore, String gridStore,String caller);
	
	void deleteTurnfullmemb(int tf_id, String caller);
	
	void auditTurnfullmemb(int tf_id, String caller);
	
	void resAuditTurnfullmemb(int tf_id, String caller);
	
	void submitTurnfullmemb(int tf_id, String caller);
	
	void resSubmitTurnfullmemb(int tf_id, String caller);
	void vastZhuanz(String gridStore,String caller);
}

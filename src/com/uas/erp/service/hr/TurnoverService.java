package com.uas.erp.service.hr;


public interface TurnoverService {
	
	void saveTurnover(String formStore, String gridStore,String caller);
	
	void updateTurnoverById(String formStore, String gridStore,String caller);
	
	void deleteTurnover(int to_id, String caller);
	
	void auditTurnover(int to_id, String caller);
	
	void resAuditTurnover(int to_id, String caller);
	
	void submitTurnover(int to_id, String caller);
	
	void resSubmitTurnover(int to_id, String caller);
	
	String confirmTurnover(String caller, String data);
}

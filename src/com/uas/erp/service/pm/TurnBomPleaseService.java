package com.uas.erp.service.pm;


public interface TurnBomPleaseService {
	void saveTurnBomPlease( String caller,String formStore, String gridStore);
	void updateTurnBomPleaseById(String caller,String formStore, String gridStore);
	void deleteTurnBomPlease(String caller,int bo_id);
	void auditTurnBomPlease(int bo_id,String caller);
	void resAuditTurnBomPlease(String caller,int bo_id);
	void submitTurnBomPlease(String caller,int bo_id);
	void resSubmitTurnBomPlease(String caller,int bo_id);
	void turnStandard(String caller, int id);
}

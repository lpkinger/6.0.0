package com.uas.erp.service.hr;

import com.uas.erp.model.Employee;


public interface TurnpositionService {
	
void saveTurnposition(String formStore, String gridStore,String caller);
	
	void updateTurnpositionById(String formStore, String gridStore,String caller);
	
	void deleteTurnposition(int re_id, String caller);
	
	void auditTurnposition(int re_id, String caller);
	
	void resAuditTurnposition(int re_id, String caller);
	
	void submitTurnposition(int re_id, String caller);
	
	void resSubmitTurnposition(int re_id, String caller);
}

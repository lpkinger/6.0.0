package com.uas.erp.service.hr;


public interface TurndecruitmentService {
	
	void saveTurndecruitment(String formStore, String gridStore,String caller);
	
	void updateTurndecruitmentById(String formStore, String gridStore,String caller);
	
	void deleteTurndecruitment(int re_id, String caller);
	
	void auditTurndecruitment(int re_id, String caller);
	
	void resAuditTurndecruitment(int re_id, String caller);
	
	void submitTurndecruitment(int re_id, String caller);
	
	void resSubmitTurndecruitment(int re_id, String caller);
}

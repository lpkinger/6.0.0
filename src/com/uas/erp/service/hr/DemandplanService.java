package com.uas.erp.service.hr;


public interface DemandplanService {
	
	void saveDemandplan(String formStore, String gridStore, String  caller);
	
	void updateDemandplanById(String formStore, String gridStore, String  caller);
	
	void deleteDemandplan(int dp_id, String  caller);
	
	void auditDemandplan(int dp_id, String  caller);
	
	void resAuditDemandplan(int dp_id, String  caller);
	
	void submitDemandplan(int dp_id, String  caller);
	
	void resSubmitDemandplan(int dp_id, String  caller);
	
	void demandTurn(String  caller, int code, String param);
	
	String vastTurnRecruitment(String caller, String data);

}

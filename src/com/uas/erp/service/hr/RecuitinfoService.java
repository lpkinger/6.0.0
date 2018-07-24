package com.uas.erp.service.hr;


public interface RecuitinfoService {
	
	void saveRecuitinfo(String formStore, String  caller);
	
	void updateRecuitinfoById(String formStore, String  caller);
	
	void deleteRecuitinfo(int or_id, String  caller);
	
	void vastWriteexam(String caller,int[] id);
	
	void vastinterview(String caller,int[] id);
	
	void vastJointalcpool(String caller,int[] id);
	
	void vastWritemark(String caller,int[] id,int[] mark);
	
	void vastInterviewmark(String caller,int[] id,int[] mark);
	
	void vastTurnrecruitplan(String caller,int[] id);
	
	String vastTurnJointalcpool(String caller, String data);
	
	String vastTurnrecruitplan(String caller, String data);

	void submitRecuitinfo(int id, String caller);

	void resSubmitRecuitinfo(int id, String caller);

	void auditRecuitinfo(int id, String caller);

	void resAuditRecuitinfo(int id, String caller);
}

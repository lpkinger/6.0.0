package com.uas.erp.service.hr;


public interface CareerapplyService {
	
	void saveCareerapply(String formStore,String gridStore, String  caller);
	
	void updateCareerapplyById(String formStore,String gridStore, String  caller);
	
	void deleteCareerapply(int or_id, String  caller);
	
	void auditCareerapply(int re_id, String  caller);
	
	void resAuditCareerapply(int re_id, String  caller);
	
	void submitCareerapply(int re_id, String  caller);
	
	void resSubmitCareerapply(int re_id, String  caller);
	
	void turnEmployee(String  caller,String param,int id);
	
	String vastTurnEmployee(String caller, String data);
}

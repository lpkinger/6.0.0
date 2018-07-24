package com.uas.erp.service.fa;



public interface BankPlanService {
	void saveBankPlan(String caller ,String formStore, String gridStore,String param2,String param3);
	void updateBankPlanById(String caller ,String formStore, String gridStore, String param2,String param3 );
	void deleteBankPlan(String caller ,int pu_id);
	String[] printBankPlan(String caller ,int pu_id,String reportName,String condition);
	void auditBankPlan(String caller ,int pu_id);
	void resAuditBankPlan(String caller ,int pu_id);
	void submitBankPlan(String caller ,int pu_id);
	void resSubmitBankPlan(String caller ,int pu_id);
	
}

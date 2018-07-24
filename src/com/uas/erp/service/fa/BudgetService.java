package com.uas.erp.service.fa;



public interface BudgetService {
	void saveBudget(String caller ,String formStore);
	void updateBudgetById(String caller ,String formStore);
	void deleteBudget(String caller ,int pu_id);
	String[] printBudget(String caller ,int pu_id,String reportName,String condition);
	
}

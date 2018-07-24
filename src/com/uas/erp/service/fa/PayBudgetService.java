package com.uas.erp.service.fa;

public interface PayBudgetService {
	void CalFKBudget(int yearmonth);

	void savePayBudget(String formStore, String gridStore, String caller);

	void updatePayBudgetById(String formStore, String gridStore, String caller);

	void deletePayBudget(int pb_id, String caller);

	void auditPayBudget(int pb_id, String caller);

	void resAuditPayBudget(int pb_id, String caller);

	void submitPayBudget(int pb_id, String caller);

	void resSubmitPayBudget(int pb_id, String caller);

	String[] printPayBudget(int pb_id, String reportName, String condition);

}

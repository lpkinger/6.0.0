package com.uas.erp.service.fa;

public interface ReceiveBudgetService {
	void CalBudget(int yearmonth);

	void saveReceiveBudget(String formStore, String gridStore, String caller);

	void updateReceiveBudgetById(String formStore, String gridStore,
			String caller);

	void deleteReceiveBudget(int rb_id, String caller);

	void submitReceiveBudget(int rb_id, String caller);

	void resSubmitReceiveBudget(int rb_id, String caller);

	void auditReceiveBudget(int rb_id, String caller);

	void resAuditReceiveBudget(int rb_id, String caller);

	String[] printReceiveBudget(int rb_id, String reportName, String condition);

}

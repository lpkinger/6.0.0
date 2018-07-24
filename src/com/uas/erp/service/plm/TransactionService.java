package com.uas.erp.service.plm;

public interface TransactionService {
	void saveTransaction(String formStore);

	void updateTransaction(String formStore);

	void deleteTransaction(int id);

	void submitTransaction(int id);

	void resSubmitTransaction(int id);

	void auditTransaction(int id);

	void resAuditTransaction(int id);
}

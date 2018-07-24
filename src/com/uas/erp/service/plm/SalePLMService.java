package com.uas.erp.service.plm;

public interface SalePLMService {
	void deleteSale(int sa_id, String caller);

	void saveSale(String formStore, String gridStore, String caller);

	void updateSale(String formStore, String gridStore, String caller);

	void auditSale(int sa_id, String caller);

	void resAuditSale(int sa_id, String caller);

	void submitSale(int sa_id, String caller);

	void resSubmitSale(int sa_id, String caller);

	void printSale(int sa_id, String caller);

	void endSale(int id, String caller);

	void resEndSale(int id, String caller);

	int turnSendNotify(int sa_id, String caller);
}

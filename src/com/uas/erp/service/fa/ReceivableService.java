package com.uas.erp.service.fa;

public interface ReceivableService {
	void saveReceivable(String formStore, String gridStore, String caller);

	void updateReceivableById(String formStore, String gridStore, String caller);

	void deleteReceivable(int dcr_id, String caller);

	void auditReceivable(int dcr_id, String caller);

	void resAuditReceivable(int dcr_id, String caller);

	void submitReceivable(int dcr_id, String caller);

	void resSubmitReceivable(int dcr_id, String caller);

	String turnBankRegister(String caller, String data);
}

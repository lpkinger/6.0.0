package com.uas.erp.service.fa;

public interface ReturnService {
	void saveReturn(String formStore, String gridStore, String caller);

	void updateReturnById(String formStore, String gridStore, String caller);

	void deleteReturn(int ccr_id, String caller);

	void submitReturn(int ccr_id, String caller);

	void resSubmitReturn(int ccr_id, String caller);

	void auditReturn(int ccr_id, String caller);

	void resAuditReturn(int ccr_id, String caller);

	String turnBankRegister(String caller, String data);
}

package com.uas.erp.service.fa;

public interface DebitContractRegisterService {
	void saveDebitContractRegister(String formStore, String caller);

	void updateDebitContractRegisterById(String formStore, String caller);

	void deleteDebitContractRegister(int dcr_id, String caller);

	void auditDebitContractRegister(int dcr_id, String caller);

	void resAuditDebitContractRegister(int dcr_id, String caller);

	void submitDebitContractRegister(int dcr_id, String caller);

	void resSubmitDebitContractRegister(int dcr_id, String caller);

}

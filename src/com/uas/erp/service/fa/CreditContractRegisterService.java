package com.uas.erp.service.fa;

public interface CreditContractRegisterService {
	void saveCreditContractRegister(String formStore, String caller);

	void updateCreditContractRegisterById(String formStore, String caller);

	void deleteCreditContractRegister(int ccr_id, String caller);

	void submitCreditContractRegister(int ccr_id, String caller);

	void resSubmitCreditContractRegister(int ccr_id, String caller);

	void auditCreditContractRegister(int ccr_id, String caller);

	void resAuditCreditContractRegister(int ccr_id, String caller);
}

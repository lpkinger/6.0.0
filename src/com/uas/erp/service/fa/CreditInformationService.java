package com.uas.erp.service.fa;

public interface CreditInformationService {
	void saveCreditInformation(String formStore, String caller);

	void updateCreditInformationById(String formStore, String caller);

	void deleteCreditInformation(int ci_id, String caller);

	void auditCreditInformation(int ci_id, String caller);

	void resAuditCreditInformation(int ci_id, String caller);

	void submitCreditInformation(int ci_id, String caller);

	void resSubmitCreditInformation(int ci_id, String caller);

}

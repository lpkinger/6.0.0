package com.uas.erp.service.fa;

public interface DebitInformationService {
	void saveDebitInformation(String formStore, String caller);

	void updateDebitInformationById(String formStore, String caller);

	void deleteDebitInformation(int di_id, String caller);

	void submitDebitInformation(int di_id, String caller);

	void resSubmitDebitInformation(int di_id, String caller);

	void auditDebitInformation(int di_id, String caller);

	void resAuditDebitInformation(int di_id, String caller);

}

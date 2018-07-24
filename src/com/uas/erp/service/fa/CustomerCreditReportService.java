package com.uas.erp.service.fa;

public interface CustomerCreditReportService {
	void saveCustomerCreditReport(String formStore, String caller);

	void updateCustomerCreditReport(String formStore, String caller);

	void deleteCustomerCreditReport(int ccr_id, String caller);

	void submitCustomerCreditReport(int ccr_id, String caller);

	void resSubmitCustomerCreditReport(int ccr_id, String caller);

	void auditCustomerCreditReport(int ccr_id, String caller);

	void resAuditCustomerCreditReport(int ccr_id, String caller);

}

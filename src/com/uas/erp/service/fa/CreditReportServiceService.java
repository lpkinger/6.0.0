package com.uas.erp.service.fa;

public interface CreditReportServiceService {
	void saveCreditReportService(String formStore, String caller);

	void updateCreditReportService(String formStore, String caller);

	void deleteCreditReportService(int crs_id, String caller);

	void submitCreditReportService(int crs_id, String caller);

	void resSubmitCreditReportService(int crs_id, String caller);

	void auditCreditReportService(int crs_id, String caller);

	void resAuditCreditReportService(int crs_id, String caller);

}

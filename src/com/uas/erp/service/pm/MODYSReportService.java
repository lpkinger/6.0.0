package com.uas.erp.service.pm;

public interface MODYSReportService {
	void deleteYSReport(int mo_id, String caller);

	void auditYSReport(int id, String caller);

	void postYSReport(String caller, int mo_id);

	void resPostYSReport(String caller, int mo_id);

	int turnMJProject(int mo_id, String caller);

	void updatestf(int ws_id, String vend);

	void resAuditYSReport(int id, String caller);
}

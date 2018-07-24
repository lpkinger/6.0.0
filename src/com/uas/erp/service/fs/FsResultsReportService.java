package com.uas.erp.service.fs;

public interface FsResultsReportService {

	void updateFsResultsReportById(String formStore, String gridStore, String caller);

	void auditFsResultsReport(int re_id, String caller);

	void resAuditFsResultsReport(int re_id, String caller);

	void submitFsResultsReport(int re_id, String caller);

	void resSubmitFsResultsReport(int re_id, String caller);

}

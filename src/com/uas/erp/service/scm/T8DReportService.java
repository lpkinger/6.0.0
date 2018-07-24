package com.uas.erp.service.scm;

public interface T8DReportService {

	void saveT8DReport(String formStore, String caller);

	void updateT8DReportById(String formStore, String caller);

	void deleteT8DReport(int re_id, String caller);

	void auditT8DReport(int re_id, String caller);

	void resAuditT8DReport(int re_id, String caller);

	void submitT8DReport(int re_id, String caller);

	void resSubmitT8DReport(int re_id, String caller);

	void checkT8DReport(int re_id, String caller);

	void resCheckT8DReport(int re_id, String caller);
}

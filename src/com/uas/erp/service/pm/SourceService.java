package com.uas.erp.service.pm;

public interface SourceService {
	void saveSource(String formStore, String caller);

	void updateSourceById(String formStore, String caller);

	void deleteSource(int sc_id, String caller);

	void auditSource(int sc_id, String caller);

	void resAuditSource(int sc_id, String caller);

	void submitSource(int sc_id, String caller);

	void resSubmitSource(int sc_id, String caller);
}

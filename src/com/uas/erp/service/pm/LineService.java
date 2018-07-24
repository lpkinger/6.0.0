package com.uas.erp.service.pm;

public interface LineService {
	void saveLine(String formStore, String caller);

	void updateLineById(String formStore, String caller);

	void deleteLine(int li_id, String caller);

	void auditLine(int li_id, String caller);

	void resAuditLine(int li_id, String caller);

	void submitLine(int li_id, String caller);

	void resSubmitLine(int li_id, String caller);
}

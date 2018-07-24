package com.uas.erp.service.scm;

public interface SawingSheetService {
	void saveSawingSheet(String formStore, String gridStore, String gridStore2, String caller);

	void updateSawingSheetById(String formStore, String gridStore, String gridStore2, String caller);

	void deleteSawingSheet(String caller, int ss_id);

	void auditSawingSheet(String caller, int ss_id);

	void resAuditSawingSheet(String caller, int ss_id);

	void submitSawingSheet(String caller, int ss_id);

	void resSubmitSawingSheet(String caller, int ss_id);

	void postSawingSheet(String caller, int ss_id);

	void resPostSawingSheet(String caller, int ss_id);

}

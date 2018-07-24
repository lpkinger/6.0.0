package com.uas.erp.service.scm;

public interface AqlService {
	void saveAql(String caller, String formStore, String gridStore);
	void updateAqlById(String caller, String formStore, String gridStore);
	void deleteAql(String caller, int al_id);
	void printAql(String caller, int al_id);
	void auditAql(String caller, int al_id);
	void resAuditAql(String caller, int al_id);
	void submitAql(String caller, int al_id);
	void resSubmitAql(String caller, int al_id);
}

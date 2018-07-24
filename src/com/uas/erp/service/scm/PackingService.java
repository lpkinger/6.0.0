package com.uas.erp.service.scm;

public interface PackingService {
	void savePacking(String formStore, String gridStore, String caller);
	void updatePackingById(String formStore, String gridStore, String caller);
	void deletePacking(int pi_id, String caller);
	String[] printPacking(int pi_id, String caller, String reportName,String condition);
	void auditPacking(int pi_id, String caller);
	void resAuditPacking(int pi_id, String caller);
	void submitPacking(int pi_id, String caller);
	void resSubmitPacking(int pi_id, String caller);
	void updateMadeIn(int pi_id);
}

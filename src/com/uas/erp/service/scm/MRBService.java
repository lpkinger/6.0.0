package com.uas.erp.service.scm;

public interface MRBService  {
	void saveMRB(String formStore, String gridStore, String caller);
	void updateMRBById(String formStore, String gridStore1, String gridStore2, String caller);
	void deleteMRB(int mr_id, String caller);
	void printMRB(int mr_id, String caller);
	void auditMRB(int mr_id, String caller);
	void resAuditMRB(int mr_id, String caller);
	void approveMRB(int mr_id, String caller);
	void resApproveMRB(int mr_id, String caller);
	void submitMRB(int mr_id, String caller);
	void resSubmitMRB(int mr_id, String caller);
}

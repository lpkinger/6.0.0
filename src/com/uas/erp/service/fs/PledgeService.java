package com.uas.erp.service.fs;

public interface PledgeService {
	void savePledge(String formStore, String  caller);
	void updatePledge(String formStore, String  caller);
	void deletePledge(int pl_id, String  caller);
	void submitPledge(int pl_id, String caller);
	void resSubmitPledge(int pl_id, String caller);
	void auditPledge(int pl_id, String caller);
	void resAuditPledge(int pl_id, String caller);
}

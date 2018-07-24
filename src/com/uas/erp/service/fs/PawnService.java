package com.uas.erp.service.fs;

public interface PawnService {
	void savePawn(String formStore, String  caller);
	void updatePawn(String formStore, String  caller);
	void deletePawn(int pl_id, String  caller);
	void submitPawn(int pl_id, String caller);
	void resSubmitPawn(int pl_id, String caller);
	void auditPawn(int pl_id, String caller);
	void resAuditPawn(int pl_id, String caller);
}

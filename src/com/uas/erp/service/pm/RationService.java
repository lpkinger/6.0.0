package com.uas.erp.service.pm;


public interface RationService {
	void updateRation(String formStore, String gridStore, String   caller);
	void submitRation(int id, String   caller);
	void resSubmitRation(int id, String   caller);
	void auditRation(int id, String   caller);
	void resAuditRation(int id, String   caller);
	void deleteRation(int id, String   caller);
}

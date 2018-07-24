package com.uas.erp.service.as;

public interface StandbyBackService {
	void saveStandbyBack(String formStore, String gridStore, String caller);
	void deleteStandbyBack(int ct_id, String caller);
	void updateStandbyBack(String formStore, String gridStore,
			String caller);
	void submitStandbyBack(int ct_id, String caller);
	void resSubmitStandbyBack(int ct_id, String caller);
	void auditStandbyBack(int ct_id, String caller);
	void resAuditStandbyBack(int ct_id, String caller);
}

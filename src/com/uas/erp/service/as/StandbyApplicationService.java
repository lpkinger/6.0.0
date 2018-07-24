package com.uas.erp.service.as;

public interface StandbyApplicationService {
	void saveStandbyApplication(String formStore, String gridStore, String caller);
	void deleteStandbyApplication(int ct_id, String caller);
	void updateStandbyApplication(String formStore, String gridStore,
			String caller);
	void submitStandbyApplication(int ct_id, String caller);
	void resSubmitStandbyApplication(int ct_id, String caller);
	void auditStandbyApplication(int ct_id, String caller);
	void resAuditStandbyApplication(int ct_id, String caller);
}

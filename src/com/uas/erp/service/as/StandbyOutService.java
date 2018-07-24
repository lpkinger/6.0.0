package com.uas.erp.service.as;

public interface StandbyOutService {
	void saveStandbyOut(String formStore, String gridStore, String caller);
	void deleteStandbyOut(int ct_id, String caller);
	void updateStandbyOut(String formStore, String gridStore,
			String caller);
	void submitStandbyOut(int ct_id, String caller);
	void resSubmitStandbyOut(int ct_id, String caller);
	void auditStandbyOut(int ct_id, String caller);
	void resAuditStandbyOut(int ct_id, String caller);
}

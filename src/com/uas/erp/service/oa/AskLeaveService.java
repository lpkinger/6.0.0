package com.uas.erp.service.oa;


public interface AskLeaveService {
	void saveAskLeave(String formStore, String caller);
	void updateAskLeave(String formStore, String caller);
	void deleteAskLeave(int al_id, String caller);
	void auditAskLeave(int al_id, String caller);
	void resAuditAskLeave(int al_id, String caller);
	void submitAskLeave(int al_id, String caller);
	void resSubmitAskLeave(int al_id, String caller);
}

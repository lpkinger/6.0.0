package com.uas.erp.service.pm;

public interface WorkShiftService {
	void saveWorkShift(String formStore, String caller);

	void updateWorkShiftById(String formStore, String caller);

	void deleteWorkShift(int ws_id, String caller);

	void auditWorkShift(int ws_id, String caller);

	void resAuditWorkShift(int ws_id, String caller);

	void submitWorkShift(int ws_id, String caller);

	void resSubmitWorkShift(int ws_id, String caller);
}

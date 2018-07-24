package com.uas.erp.service.oa;


public interface AppOvertimeService {
	void saveAppOvertime(String formStore, String caller);
	void updateAppOvertime(String formStore, String caller);
	void deleteAppOvertime(int ao_id, String caller);
	void auditAppOvertime(int ao_id, String caller);
	void resAuditAppOvertime(int ao_id, String caller);
	void submitAppOvertime(int ao_id, String caller);
	void resSubmitAppOvertime(int ao_id, String caller);
}

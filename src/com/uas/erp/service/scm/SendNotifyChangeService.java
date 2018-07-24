package com.uas.erp.service.scm;

public interface SendNotifyChangeService {
	void saveSendNotifyChange(String formStore, String gridStore, String caller);
	void updateSendNotifyChangeById(String formStore, String gridStore, String caller);
	void deleteSendNotifyChange(int sc_id, String caller);
	void auditSendNotifyChange(int sc_id, String caller);
	void resAuditSendNotifyChange(int sc_id, String caller);
	void submitSendNotifyChange(int sc_id, String caller);
	void resSubmitSendNotifyChange(int sc_id, String caller);
	String[] printSendNotifyChange(int sc_id, String caller,String reportName,String condition);
}

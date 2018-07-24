package com.uas.erp.service.scm;

public interface AcceptNotifyService {
	void saveAcceptNotify(String caller, String formStore, String gridStore);
	void updateAcceptNotifyById(String caller, String formStore, String gridStore);
	void deleteAcceptNotify(String caller, int an_id);
	String[] printAcceptNotify(int an_id, String caller, String reportName, String condition);
	void auditAcceptNotify(String caller, int an_id);
	void resAuditAcceptNotify(String caller, int an_id);
	void submitAcceptNotify(String caller, int an_id);
	void resSubmitAcceptNotify(String caller, int an_id);
	int turnVerifyApply(String caller, int an_id);
	String turnProdio(String caller, int an_id);
	void saveAcceptNotifyQty(String data);
	void backAll(int id);
}

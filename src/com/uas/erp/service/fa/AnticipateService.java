package com.uas.erp.service.fa;

public interface AnticipateService {
	void createAnticipate(String date, String cucode, String emcode, String dpcode);

	void saveAnticipate(String formStore, String gridStore, String caller);

	void updateAnticipateById(String formStore, String gridStore, String caller);

	void deleteAnticipate(int an_id, String caller);

	String[] printAnticipate(int an_id, String reportName, String condition, String caller);

	void auditAnticipate(int an_id, String caller);

	void resAuditAnticipate(int an_id, String caller);

	void submitAnticipate(int an_id, String caller);

	void resSubmitAnticipate(int an_id, String caller);

	void refreshAnticipateBack(String caller, String from, String to);

}

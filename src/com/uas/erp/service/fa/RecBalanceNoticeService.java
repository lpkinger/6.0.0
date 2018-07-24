package com.uas.erp.service.fa;

public interface RecBalanceNoticeService {
	void saveRecBalanceNotice(String formStore, String gridStore, String caller);

	void updateRecBalanceNoticeById(String formStore, String gridStore, String caller);

	void deleteRecBalanceNotice(int rb_id, String caller);

	String[] printRecBalanceNotice(int rb_id, String reportName, String condition, String caller);

	void auditRecBalanceNotice(int rb_id, String caller);

	void resAuditRecBalanceNotice(int rb_id, String caller);

	void submitRecBalanceNotice(int rb_id, String caller);

	void resSubmitRecBalanceNotice(int rb_id, String caller);

	String turnAccountRegister(int rb_id, String catecode, String caller);

	String turnBillAR(int rb_id, String catecode, String caller);

	void catchAB(String caller, String formStore, String startdate, String enddate, String bicode);

	void cleanAB(String caller, String formStore);

}

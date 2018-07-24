package com.uas.erp.service.fa;

public interface ARCheckService {
	void saveARCheck(String formStore, String gridStore);

	void updateARCheckById(String formStore, String gridStore);

	void deleteARCheck(int ac_id);

	String[] printARCheck(int ac_id, String reportName, String condition);

	void auditARCheck(int ac_id);

	void resAuditARCheck(int ac_id);

	void submitARCheck(int ac_id);

	void resSubmitARCheck(int ac_id);

	void accountedARCheck(int ac_id);

	void resAccountedARCheck(int ac_id);

	void confirmARCheck(int ac_id);

	void cancelARCheck(int ac_id);

	void updateDetailInfo(String data, String caller);

	String turnBill(String caller, String data);

	void submitARCheckConfirm(int id);

	void resSubmitARCheckConfirm(int id);

	String turnRecBalanceNotice(int id, String data, String caller);
}

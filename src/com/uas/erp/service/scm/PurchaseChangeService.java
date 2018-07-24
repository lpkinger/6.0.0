package com.uas.erp.service.scm;

public interface PurchaseChangeService {
	void savePurchaseChange(String formStore, String gridStore, String caller);

	void updatePurchaseChangeById(String formStore, String gridStore, String caller);

	void deletePurchaseChange(int pc_id, String caller);

	void auditPurchaseChange(int pc_id, String caller);

	/**
	 * 供应商同意之后
	 * 
	 * @param pc_code
	 */
	void onChangeAgreed(String pc_code);

	void resAuditPurchaseChange(int pc_id, String caller);

	void submitPurchaseChange(int pc_id, String caller);

	void resSubmitPurchaseChange(int pc_id, String caller);

	String[] printPurchaseChange(int pc_id, String caller, String reportName, String condition);

	/**
	 * 需要供应商变更
	 * 
	 * @param changeId
	 *            变更单ID
	 */
	void needCheck(Integer changeId);
}

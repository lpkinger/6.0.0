package com.uas.erp.service.scm;

public interface ReturnApplyService {
	void saveReturnApply(String formStore, String gridStore, String caller);
	void updateReturnApplyById(String formStore, String gridStore, String caller);
	void deleteReturnApply(int ra_id, String caller);
	void auditReturnApply(int ra_id, String caller);
	void resAuditReturnApply(int ra_id, String caller);
	void submitReturnApply(int ra_id, String caller);
	void resSubmitReturnApply(int ra_id, String caller);
	String turnReturn(String caller, String data);
	String[] printReturnApply(int ra_id, String reportName, String condition, String caller);
}

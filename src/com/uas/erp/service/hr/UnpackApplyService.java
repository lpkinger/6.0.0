package com.uas.erp.service.hr;


public interface UnpackApplyService {
	void auditUnpackApply(int ua_id, String caller);
	void resAuditUnpackApply(int ua_id, String caller);
	void confirmUnpackApply(int ua_id, String caller);
}

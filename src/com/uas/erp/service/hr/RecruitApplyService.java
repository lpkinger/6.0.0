package com.uas.erp.service.hr;


public interface RecruitApplyService {
	void auditRecruitApply(int ra_id, String caller);
	void resAuditRecruitApply(int ra_id, String caller);
	void endRecruitApply(int ra_id, String caller);
}

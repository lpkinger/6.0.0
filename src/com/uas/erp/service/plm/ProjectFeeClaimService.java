package com.uas.erp.service.plm;

public interface ProjectFeeClaimService {
	void saveProjectFeeClaim(String formStore, String gridStore, String caller);

	void deleteProjectFeeClaim(int id, String caller);

	void updateProjectFeeClaim(String formStore, String param, String caller);

	void auditProjectFeeClaim(int id, String caller);

	void submitProjectFeeClaim(int id, String caller);

	void resSubmitProjectFeeClaim(int id, String caller);

	void resAuditProjectFeeClaim(int id, String caller);
}

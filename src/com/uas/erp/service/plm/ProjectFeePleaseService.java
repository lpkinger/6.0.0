package com.uas.erp.service.plm;

public interface ProjectFeePleaseService {
	void saveProjectFeePlease(String formStore, String gridStore, String caller);

	void deleteProjectFeePlease(int id, String caller);

	void updateProjectFeePlease(String formStore, String param, String caller);

	void auditProjectFeePlease(int id, String caller);

	void submitProjectFeePlease(int id, String caller);

	void resSubmitProjectFeePlease(int id, String caller);

	void resAuditProjectFeePlease(int id, String caller);

	int turnProjectFee(int pf_id, String caller);
}

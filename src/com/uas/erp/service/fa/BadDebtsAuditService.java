package com.uas.erp.service.fa;

public interface BadDebtsAuditService {
	void saveBadDebtsAudit(String formStore, String gridStore, String caller);

	void updateBadDebtsAuditById(String formStore, String gridStore,
			String caller);

	void deleteBadDebtsAudit(int bda_id, String caller);

	void auditBadDebtsAudit(int bda_id, String caller);

	void resAuditBadDebtsAudit(int bda_id, String caller);

	void submitBadDebtsAudit(int bda_id, String caller);

	void resSubmitBadDebtsAudit(int bda_id, String caller);

	String[] printBadDebtsAudit(int bda_id, String reportName, String condition);

	int turnRecBalanceIMRE(int bda_id, String caller);
}

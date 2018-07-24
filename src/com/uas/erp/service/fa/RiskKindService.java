package com.uas.erp.service.fa;

public interface RiskKindService {
	void saveRiskKind(String formStore, String caller);

	void updateRiskKind(String formStore, String caller);

	void deleteRiskKind(int rk_id, String caller);

	void submitRiskKind(int rk_id, String caller);

	void resSubmitRiskKind(int rk_id, String caller);

	void auditRiskKind(int rk_id, String caller);

	void resAuditRiskKind(int rk_id, String caller);

}

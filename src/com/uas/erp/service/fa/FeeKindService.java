package com.uas.erp.service.fa;

public interface FeeKindService {
	void saveFeeKind(String formStore, String caller);

	void updateFeeKind(String formStore, String caller);

	void deleteFeeKind(int fk_id, String caller);

	void submitFeeKind(int fk_id, String caller);

	void resSubmitFeeKind(int fk_id, String caller);

	void auditFeeKind(int fk_id, String caller);

	void resAuditFeeKind(int fk_id, String caller);

}

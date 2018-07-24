package com.uas.erp.service.fa;

public interface ReceivablePlanService {
	void saveReceivablePlan(String formStore, String gridStore, String caller);

	void updateReceivablePlanById(String formStore, String gridStore,
			String caller);

	void deleteReceivablePlan(int dcr_id, String caller);

	void submitReceivablePlan(int dcr_id, String caller);

	void resSubmitReceivablePlan(int dcr_id, String caller);

	void auditReceivablePlan(int dcr_id, String caller);

	void resAuditReceivablePlan(int dcr_id, String caller);

}

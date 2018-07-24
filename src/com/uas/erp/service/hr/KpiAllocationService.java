package com.uas.erp.service.hr;


public interface KpiAllocationService {
	void saveKpiAllocation(String formStore, String gridStore, String caller);
	void deleteKpiAllocation(int ka_id, String caller);
	void updateKpiAllocationById(String formStore,String gridStore, String caller);
	void submitKpiAllocation(int ka_id, String caller);
	void resSubmitKpiAllocation(int ka_id, String caller);
	void auditKpiAllocation(int ka_id, String caller);
	void resAuditKpiAllocation(int ka_id, String caller);
}

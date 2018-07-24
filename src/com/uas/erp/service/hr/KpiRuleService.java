package com.uas.erp.service.hr;

public interface KpiRuleService {
	void saveKpiRule(String formStore, String gridStore,String caller);
	void updateKpiRule(String formStore, String gridStore, String caller);
	void deleteKpiRule(int kr_id, String caller);
	void testSQL(String sql,String caller);
}

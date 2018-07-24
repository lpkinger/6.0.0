package com.uas.erp.service.hr;

public interface KpidesigngradeService {
	void saveKpidesigngrade(String formStore, String gridStore,String caller);
	void updateKpidesigngrade(String formStore, String gridStore, String caller);
	void deleteKpidesigngrade(int kg_id, String caller);
}

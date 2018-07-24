package com.uas.erp.service.hr;

import com.uas.erp.model.GridPanel;

public interface KpiApplyService {
	
	void saveKpiApply(String formStore, String  caller);
	
	void updateKpiApply(String formStore,String gridStore, String  caller);
	
	void deleteKpiApply(int ka_id, String  caller);
	
	void auditKpiApply(int ka_id, String  caller);
	
	void resAuditKpiApply(int ka_id, String  caller);
	
	void submitKpiApply(int ka_id, String  caller);
	
	void resSubmitKpiApply(int ka_id, String  caller);
	
	GridPanel getGridPanel(String caller, String condition, Integer start,Integer end, Integer _m);
}

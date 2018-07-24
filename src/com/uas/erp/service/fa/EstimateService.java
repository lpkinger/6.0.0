package com.uas.erp.service.fa;



public interface EstimateService {
	void turnEstimate();
	void saveEstimate(String caller ,String formStore, String gridStore);
	void updateEstimate(String caller ,String formStore, String gridStore);
	void deleteEstimate(String caller ,int gs_id);
	void printEstimate(String caller ,int gs_id);
	void auditEstimate(String caller ,int gs_id);
	void resAuditEstimate(String caller ,int gs_id);
	void submitEstimate(String caller ,int gs_id);
	void resSubmitEstimate(String caller ,int gs_id);
	void postEstimate(String caller ,int gs_id);
	void resPostEstimate(String caller ,int gs_id);
}

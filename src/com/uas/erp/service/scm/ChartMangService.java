package com.uas.erp.service.scm;

public interface ChartMangService {
	void saveChartMang(String formStore);
	void updateChartMang(String formStore);
	void deleteChartMang(int ct_id);
	void auditChartMang(int ct_id);
	void resAuditChartMang(int ct_id);
	void submitChartMang(int ct_id);
	void resSubmitChartMang(int ct_id);
}

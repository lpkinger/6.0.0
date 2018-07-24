package com.uas.erp.service.scm;

public interface MakeForecastService {
	void saveMakeForecast(String formStore, String gridStore, String caller);
	void updateMakeForecastById(String formStore, String gridStore, String caller);
	void deleteMakeForecast(int mf_id, String caller);
	void printMakeForecast(int mf_id, String caller);
	void auditMakeForecast(int mf_id, String caller);
	void resAuditMakeForecast(int mf_id, String caller);
	void submitMakeForecast(int mf_id, String caller);
	void resSubmitMakeForecast(int mf_id, String caller);
}

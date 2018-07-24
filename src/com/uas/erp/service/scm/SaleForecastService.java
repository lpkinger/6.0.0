package com.uas.erp.service.scm;

import net.sf.json.JSONObject;

public interface SaleForecastService {
	void saveSaleForecast(String formStore, String gridStore, String caller);

	void updateSaleForecastById(String formStore, String gridStore, String caller);

	void deleteSaleForecast(int sf_id, String caller);

	void auditSaleForecast(int sf_id, String caller);

	void resAuditSaleForecast(int sf_id, String caller);

	void submitSaleForecast(int sf_id, String caller);

	void resSubmitSaleForecast(int sf_id, String caller);

	void saveSaleForecastChangedate(String caller, String data);

	void printSaleForeCast(int id, String caller);

	String[] printSaleForecast(int sa_id, String caller, String reportName, String condition);

	void openMrp(int id, String caller);

	void closeMrp(int id, String caller);

	JSONObject getShortConfig(String condition);

	void updateShortForecast(String formStore, String param, String caller);

	void UpdateForecastQty(String data, String caller);

	void saleforecastdataupdate(int id, String caller);
	
	void splitSaleForecast(String formdata, String data);

}

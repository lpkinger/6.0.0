package com.uas.erp.service.scm;

import net.sf.json.JSONObject;

public interface PreSaleForecastService {
	void savePreSaleForecast(String formStore, String gridStore, String caller);
	void updatePreSaleForecastById(String formStore, String gridStore, String caller);
	void deletePreSaleForecast(int sf_id, String caller);
	void deletePreSaleForecastDetail(String sd_id, String caller);
	JSONObject getPreConfig(String condition);
	void updatePreForecast(String formStore, String param, String caller);
	void savePreSaleForecastChangedate(String caller, String data);
	void auditPreSaleForecast(int sf_id, String caller);
	void resAuditPreSaleForecast(int sf_id, String caller);
	void submitPreSaleForecast(int sf_id, String caller);
	void resSubmitPreSaleForecast(int sf_id, String caller);
	String copyPreSaleForecast(int sf_id, String caller,String forecast,String weeks,String weeke,String months,String monthe,String days,String daye);

	
}

package com.uas.erp.service.drp;



public interface SaleForecastAskService {
	void saveSaleForecastAsk(String formStore, String gridStore,  String caller);
	void updateSaleForecastAskById(String formStore, String gridStore,  String caller);
	void deleteSaleForecastAsk(int sf_id,  String caller);
	void auditSaleForecastAsk(int sf_id,  String caller);
	void resAuditSaleForecastAsk(int sf_id,  String caller);
	void submitSaleForecastAsk(int sf_id,  String caller);
	void resSubmitSaleForecastAsk(int sf_id,  String caller);
	void saveSaleForecastAskChangedate(String caller,String data);
	void printSaleForecastAsk(int id,String caller);
	void endSaleForecastAsk(int id,String caller);
	void resEndSaleForecastAsk(int id,String caller);
	String[] printSaleForecastAsk(int sa_id,  String caller,String reportName,String condition);

}

package com.uas.erp.service.pm;

import net.sf.json.JSONObject;

import org.apache.poi.ss.usermodel.Workbook;

public interface WCPlanService {
	void saveWCPlan(String formStore,String param,String caller);
	void deleteWCPlan(int id ,String caller);
	void updateWCPlan(String formStore,String param,String caller);
	void submitWCPlan(int id ,String caller);
	void resSubmitWCPlan(int id,String caller);
	void auditWCPlan(int id,String caller);
	void resAuditWCPlan(int id,String caller);
	boolean ImportExcel(int id, Workbook wbs, String substring, String caller);
	void deleteAllDetails(int id, String caller);
	void loadMake(String caller, String data, int wc_id);
	String RunLackMaterial(String code, String caller);
	void RunLackWip(String code, String caller);
	JSONObject getDateRange(String condition);
	void loadAllMakeByCondition(String caller, int wc_id, String condition);
	void loadSale(String caller,String data,int wc_id);
	void loadAllSaleByCondition(String caller,int wc_id,String condition);
	void throwPurchaseNotify(String caller,String data, String condition); 
	String ThrowWipNeed(String caller,String data, String condition);
	void loadSaleForecast(String caller,String data,int wc_id);
	void loadAllSaleForecastByCondition(String caller,int wc_id,String condition);
}

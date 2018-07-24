package com.uas.erp.service.oa;

import net.sf.json.JSONObject;

public interface DailyPlanService {
	void saveDailyPlan(String formStore, String gridStore, String  caller);

	void updateDailyPlanById(String formStore, String gridStore, String  caller);

	void deleteDailyPlan(int dp_id, String  caller);

	void auditDailyPlan(int dp_id, String  caller);

	void resAuditDailyPlan(int dp_id, String  caller);

	void submitDailyPlan(int dp_id, String  caller);

	void resSubmitDailyPlan(int dp_id, String  caller);

	void endDailyPlan(int dp_id, String  caller);

	void resEndDailyPlan(int dp_id, String  caller);

	String[] printDailyPlan(int dp_id, String  caller, String reportName, String condition);

	void getPrice(int dp_id);
	
	void getStandardPrice(int dp_id);

	void vastDeleteDailyPlan(int[] id, String  caller);

	JSONObject copyDailyPlan(int id, String caller);

	//void getMakeVendorPrice(int ma_id, String  caller);

	/**
	 * 同步到香港万利达
	 * 
	 * @param caller
	 * @param data
	 * @param language
	 * @param employee
	 */
	void syncDailyPlan(String caller, String data);

	/**
	 * 刷新同步状态
	 * 
	 * @param caller
	 * @param id
	 */
	void resetSyncStatus(String caller, Integer id);

	void updateVendorBackInfo(String data, String caller);
}

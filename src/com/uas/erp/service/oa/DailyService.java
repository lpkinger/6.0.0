package com.uas.erp.service.oa;

import net.sf.json.JSONObject;

public interface DailyService {
	void saveDaily(String formStore, String gridStore, String  caller);

	void updateDailyById(String formStore, String gridStore, String  caller);

	void deleteDaily(int pu_id, String  caller);

	void auditDaily(int pu_id, String  caller);

	void resAuditDaily(int pu_id, String  caller);

	void submitDaily(int pu_id, String  caller);

	void resSubmitDaily(int pu_id, String  caller);

	void endDaily(int pu_id, String  caller);

	void resEndDaily(int pu_id, String  caller);

	String[] printDaily(int pu_id, String  caller, String reportName, String condition);

	void getPrice(int pu_id);
	
	void getStandardPrice(int pu_id);

	void vastDeleteDaily(int[] id, String  caller);

	JSONObject copyDaily(int id, String caller);

	void getMakeVendorPrice(int ma_id, String  caller);

	/**
	 * 同步到香港万利达
	 * 
	 * @param caller
	 * @param data
	 * @param language
	 * @param employee
	 */
	void syncDaily(String caller, String data);

	/**
	 * 刷新同步状态
	 * 
	 * @param caller
	 * @param id
	 */
	void resetSyncStatus(String caller, Integer id);

	void updateVendorBackInfo(String data,String caller);
}

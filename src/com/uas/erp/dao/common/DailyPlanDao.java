package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import com.uas.erp.model.Employee;

public interface DailyPlanDao {
	void updatePreDailyPlan(String dp_code, String dp_date);

	void updateDailyPlanPlan(int dp_id);

	String checkPlanQty(int dp_id);

	String checkPPcode(int dp_id);

	Object[] turnAcc(String language, String vcode, String curr, String apCode,
			Employee employee, String caller);

	void turnAccdetail(String no, int pdid, int anid, int detno, Double qty,
			Employee employee, String language);

	//int turnAccNotify(int id, String language, Employee employee);

	int newVerifyApply(Employee employee, String language);

	String turnAccept(String caller, List<Map<Object, Object>> maps,
			Employee employee, String language);

	boolean checkPdStatus(int puid, String status);

	void deleteDailyPlan(int id);

	//void restoreApplicationWithQty(int pdid, Double uqty, String language);

	String detailTurnPurcProdIO(String no, int pdid, int detno, Double qty,
			Employee employee, String language);

	JSONObject newProdIO(String curr, String vecode, String piclass,
			Employee employee, String language, String caller,
			String currentyearmonth);

	JSONObject getDailyPlanPrice(String vendcode, String prodcode,
			String currency, String kind, double qty);

	void getPrice(int dp_id);

	void getPrice(String dp_code);

	/**
	 * 采购转入收料之前， <br>
	 * 1.判断采购单状态 <br>
	 * 2.判断thisqty ≤ qty - yqty
	 */
	void checkPdYqty(List<Map<Object, Object>> datas);

	void getPutype(int dp_id);

	JSONObject getPriceVendor(String prodcode, String kind, double qty);
	/**
	 * 更新采购单明细已收料数量pd_yqty（包括直接验收数量） by zhongyl ifall =-1 则更新所有未交的采购单 否则 更新pd_id in (pdidstr)
	 */ 
	void updatePurcYQTY(int ifall, String pdidstr) ; 
	/**
	 * 更新采购单明细当前已通知数 (已发通知未收料部分) by zhongyl ifall =-1 则更新所有未交的采购单 否则 更新pd_id in (pdidstr)
	 */
	void updatePurcYNotifyQTY(int ifall, String pdidstr);

	/**
	 * 同步采购单到万利达香港(DB in SqlServer)
	 * 
	 * @param dp_id
	 */
	void syncPurcToSqlServer(int dp_id);
	
	/**
	 * 万利达刷新同步状态(DB in SqlServer)
	 * 
	 * @param dp_id
	 */
	void resetPurcSyncStatus(int dp_id);
}

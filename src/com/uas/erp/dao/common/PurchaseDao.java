package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface PurchaseDao {
	void updatePrePurchase(String pu_code, String pu_date);

	void updatePurchasePlan(int pu_id);

	String checkPlanQty(int pu_id);

	String checkPPcode(int pu_id);

	int newVerifyApply();

	void udpatestatus(int pdid);

	void udpateturnstatus(int pdid);

	boolean checkPdStatus(int puid, String status);

	void deletePurchase(int id);

	void restoreApplicationWithQty(int pdid, Double uqty);

	JSONObject newProdIO(String curr, String vecode, String piclass, String caller, String currentyearmonth);

	JSONObject getPurchasePrice(String vendcode, String prodcode, String currency, String kind, double qty, String pu_date);
	
	JSONObject getMCPurchasePrice(String vendcode, String prodcode, String currency, Integer pd_id, double qty);

	void getPrice(int pu_id);

	void getPrice(String pu_code);

	/**
	 * 采购转入收料之前 <br>
	 * 1.判断采购单状态 <br>
	 * 2.判断thisqty ≤ qty - yqty
	 */
	void checkPdYqty(List<Map<Object, Object>> datas);

	/**
	 * 采购单转验收单之前 <br>
	 * 1.判断采购单状态 <br>
	 * 2.判断thisqty ≤ qty - yqty
	 */
	void checkqty(List<Map<Object, Object>> datas);

	void getPutype(int pu_id);

	JSONObject getPriceVendor(String prodcode, String kind, double qty);

	JSONObject getMakePrice(String prodcode, String kind, double qty);

	/**
	 * 更新采购单明细已收料数量pd_yqty（包括直接验收数量） by zhongyl ifall =-1 则更新所有未交的采购单 否则 更新pd_id
	 * in (pdidstr)
	 */
	void updatePurcYQTY(int ifall, String pdidstr);

	/**
	 * 更新采购单明细当前已通知数 (已发通知未收料部分) by zhongyl ifall =-1 则更新所有未交的采购单 否则 更新pd_id in
	 * (pdidstr)
	 */
	void updatePurcYNotifyQTY(int ifall, String pdidstr);

	/**
	 * 同步采购单到万利达香港(DB in SqlServer)
	 * 
	 * @param pu_id
	 */
	void syncPurcToSqlServer(int pu_id);

	/**
	 * 万利达刷新同步状态(DB in SqlServer)
	 * 
	 * @param pu_id
	 */
	void resetPurcSyncStatus(int pu_id);

	// 取价原则：抓取最近一次采购单单价
	void getLastPrice(int pu_id);

	void restoreYqty(double tqty, Integer adid);
	

	JSONObject getPriceVendor_check(String prodcode, String kind, double qty,String vendcode);
	
	void getPriceByAccuqty(int pu_id);
}

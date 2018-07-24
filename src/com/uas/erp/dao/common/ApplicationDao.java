package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface ApplicationDao {
	int turnPurchase(int id);
	String newPurchase(String type);
	String getPuCodeBySourceCode(int id);
	JSONObject newPurchaseWithVendor(String type, int vendid, String vendcode, String vendname,String conKind,String currency);
	void getVendor(int[] id);
	void checkAdQty(int adid);
	String[] postApplication(int[] id, String from, String to);
	/**
	 * 请购转入采购之前，判断thisqty ≤ qty - yqty
	 */
	void checkAdYqty(List<Map<Object, Object>> datas);
}

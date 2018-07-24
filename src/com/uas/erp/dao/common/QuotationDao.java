package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface QuotationDao {
	int turnSale(int id);
	String newSale();
	String getSaCodeBySourceCode(int id);
	JSONObject newSaleWithCustomer(int custid, String custcode, String custname, String currency, String payments);
	void getCustomer(int[] id);
	void checkAdQty(int qdid);
	/**
	 * 报价单转入销售单之前，判断thisqty ≤ qty - yqty
	 */
	void checkQdYqty(List<Map<Object, Object>> datas);
}

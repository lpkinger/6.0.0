package com.uas.erp.dao.common;

import java.util.List;
import java.util.Map;
import net.sf.json.JSONObject;

public interface ModAlterDao {
	int turnFeePlease(int id, double aldamount);
	List<Map<String, Object>>  turnPriceMould(int id);
    JSONObject turnMouldSale(int id);
}

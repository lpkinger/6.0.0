package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface PriceMouldDao {
	JSONObject turnInquiry(int id, String vendcode, String caller);

	int turnPurMould(int id, String pricecolumn, String returncolumn, String caller);
}

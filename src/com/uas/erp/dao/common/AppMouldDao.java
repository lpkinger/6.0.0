package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface AppMouldDao {
	JSONObject turnPriceMould(Object app_id);

	JSONObject turnMouldSale(int id);

	JSONObject newPurMould(Object vendcode, Object adid, Object pmcode, String caller);

	void toAppointedPurMould(int pm_id, String pm_code, Object ad_id, int detno, String type);

	void checkAdQty(int adid);

	void toAppointedPriceMould(int pd_id, Object pd_code, int ad_id, int detno);
}

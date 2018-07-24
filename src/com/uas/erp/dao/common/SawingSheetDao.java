package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface SawingSheetDao {
	JSONObject turnProdInOut(int id, String piclass);

	void turnProdIODetail(int id, String piclass, Object pi_id, Object pi_inoutno);
}

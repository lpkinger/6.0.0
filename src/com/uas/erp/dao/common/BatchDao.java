package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface BatchDao {
	JSONObject turnQUABatch();
	int toAppointedQUABatch(String qba_code, int ba_id);
	
	JSONObject turnBoChu();
	int toAppointedBoChu(int piid, String no, int qbd_id, Double qty);
}

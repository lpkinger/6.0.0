package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface MouldFeePleaseDao {
	JSONObject turnAccountRegister(int id, Object thisamount);

	JSONObject turnBillAP(int id, Object thisamount);

	JSONObject turnBillARChange(int id, Object thisamount);

	void restoreYamount(double tqty, String pmcode, Integer pddetno, Object pmddetno);

	void restoreWithAmount(int mfdid, Double f, Object pmcode, Object pddetno, Object pmddetno);

	void deleteMouldFeePlease(int id);

	void restorePucMould(int mfdid);
}

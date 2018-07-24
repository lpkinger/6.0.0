package com.uas.erp.dao.common;

import net.sf.json.JSONObject;

public interface InvoiceDao {
	void detailTurnPaInDetail(String no, String piids, Object inid, Object piid);
	JSONObject newPaIn(Object pi_id, String inoutno, String caller);
	JSONObject newPaInwithno(Object pi_id, String inoutno, String no, String caller);
}

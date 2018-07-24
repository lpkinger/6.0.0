package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

public interface LedgerService {
	List<Map<String, Object>> getGeneralLedger(String condition);

	List<Map<String, Object>> getGLDetail(String condition);

	List<Map<String, Object>> getGeneralLedgerSingle(String condition);

	List<Map<String, Object>> getColumn(String condition);

	List<Map<String, Object>> getDeptDetail(String condition);

	List<Map<String, Object>> getVoucherSum(String condition);

	int getVoucherCount(String condition);

	boolean preWrite(String bym, String eym, int now, JSONObject d);
}

package com.uas.pda.service;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Page;

public interface PdaCheckService {

	List<Map<String, Object>> makeMaterialCheck(String pr_code, String wh_code);

	Map<String, Object> barcodeCheck(String barcode, String whcode);

	Map<String, Object> packageCheck(String outboxCode);

	List<Map<String, Object>> makeFinishCheck(String makeCode);

	List<Map<String, Object>> orderFinishCheck(String salecode);

	List<Map<String, Object>> checkPO(String or_code);

	List<Map<String, Object>> makeMaterialDetail(String pr_code, String bar_prodcode, String wh_code, String bar_location);

}

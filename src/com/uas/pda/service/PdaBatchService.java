package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaBatchService {

	public Map<String, Object> getBarcodeData(String code, boolean pr_ismsd);

	public List<Map<String ,Object>> breakingBatch(String or_barcode, Double or_remain, Double bar_remain,String reason);

	public Map<String, Object> combineBatch(String data,double total_remain);

	public Map<String, Object> searchPackageData(String data);

	public List<Map<String ,Object>> breakingPackage(String data, String param);

	public String outboxCodeMethod(String pr_code);

	public Map<String, Object> getOutBoxData(String outBox);

	public Map<String, Object> backBreaking(int sourceid, String bar_ids);

	public Map<String, Object> getSonBarcode(String barcode);
	

}

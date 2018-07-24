package com.uas.pda.service;

import java.util.Map;

import com.uas.erp.model.Page;

public interface PdaCountingService {

	public Page<Map<String, Object>> getCountingData(String st_code);
	
	public Map<String , Object> getBarData (String bar_code,String bar_whcode,String st_code);
	
	public void saveBarcode(String data);

	public Map<String , Object> serialSearch(String code, String whcode,String st_code);

	public Map<String , Object> outboxSearch(String code, String whcode,String st_code);
}

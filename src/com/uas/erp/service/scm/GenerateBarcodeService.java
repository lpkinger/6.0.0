package com.uas.erp.service.scm;

import java.util.List;
import java.util.Map;

public interface GenerateBarcodeService {

	public String getBarFormStore(String caller,String Condition);

	public void saveBarcode(String caller,String gridStore);
	
	public void batchGenBarcode(String caller,int id,String data);
	
	public void deleteAllBarDetails(String caller, Integer pi_id,String biids);
	
	public List<Map<String,Object>> getDatasFields(String condition);

	public void batchGenBarOBcode(String caller, String formStore);
	
	public void freezeBarcode(String caller, String condition);
	
	public void releaseBarcode(String caller, String condition);
	
	public List<Map<String ,Object>> breakingBatch(String or_barcode, Double or_remain, Double bar_remain);
	
	public List<Map<String ,Object>> combiningAndBreaking(String ids, Double total_remain,Double zxbzs, String every_remain);
}

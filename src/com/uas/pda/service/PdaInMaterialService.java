package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaInMaterialService {

	List<Map<String, Object>> fuzzySearch(String inoutNo, String whcode);

	List<Map<String, Object>> getProdIn(String inoutNo, String whcode);

	Map<String,Object> getNeedGetList(Integer id, String whcode);

	String  saveBarcode(String data);

	String deleteDetail(Integer bi_piid, String barcode, String outboxcode, String whcode);

	List<Map<String, Object>>  getCheckProdIn(String inoutNo, String whcode);
	
	String confirmIn(Integer bi_piid, String whcode);
	
	Map<String,Object> getCodeData(String type, Integer id, String whcode, String code);

}

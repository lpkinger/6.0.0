package com.uas.pdaio.service;

import java.util.List;
import java.util.Map;

public interface PdaioOutService {
	
	List<Map<String, Object>> fuzzySearch(String inoutNo);
	
	Map<String,Object> getNextData(Integer pi_id );
	
	List<Map<String, Object>> getProdinoutList(String condition,Integer page,Integer pageSize);
	
	List<Map<String, Object>> getBarcodeDetail(Integer piid,Integer page,Integer pageSize,String condition);

	List<Map<String, Object>> getProdInoutQtySum(Integer piid,Integer page,Integer pageSize);

	Map<String, Object> deleteBarcode(Integer piid, String type, Integer biid);

	Map<String, Object> getProdOut(String inoutNo);

	Map<String, Object> collectBarcode(Integer pi_id,String barcode);
	
	Map<String, Object> revokeBarcode(Integer pi_id,String barcode);

	List<Map<String, Object>> getProdOutStatus(String ids);

}

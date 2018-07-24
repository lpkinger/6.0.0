package com.uas.pdaio.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface PdaioInService {

	Map<String, Object> addProdinout(String pi_class, String pi_cardcode, String pi_whcode,HttpSession session);

	Map<String, Object> getWhcode(String condition);

	Map<String, Object> getVendor(String condition,Integer page,Integer pageSize);
	
	Map<String, Object> addProdiodetail(String inoutno);

	Map<String, Object> getBarcodeInfo(String inoutno,String barcode,Integer allowRepeat);

	List<Map<String, Object>> getProdinoutList(String condition,Integer page,Integer pageSize);

	Map<String, Object> deleteInoutAndDetail(Integer piid);

	List<Map<String, Object>> getBarcodeDetail(Integer piid,Integer page,Integer pageSize,String condition);

	List<Map<String, Object>> getProdInoutQtySum(Integer piid,Integer page,Integer pageSize);

	Map<String, Object> deleteBarcode(Integer piid, String type, Integer biid);

	Map<String, Object> revokeBarcode(String inoutno);

	Map<String, Object> getLatestProdinout(String emcode);
	
	Map<String, Object> updatePiCardcde(Integer piid,String newVendor);
	
}

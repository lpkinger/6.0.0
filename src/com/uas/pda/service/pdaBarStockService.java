package com.uas.pda.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface pdaBarStockService {

	 List<Map<String, Object>> getBarStockList (Integer page, Integer  pagesize);

	 List <Map<String, Object>> search (String condition,Integer page ,Integer pagesize);
	 
	 Map<String, Object> getBarStockByProdcode (Integer id,Integer page,Integer pagesize,String condition);
	 
	 List<Map<String, Object>> getBarStockBatch  (Integer id,String bsd_prodcode);
	 
	 String newBarcode(HttpServletRequest request,Integer id,String bsd_prodcode,boolean ifprint, String data);
	 
	 Map<String, List<Map<String, Object>>> getHaveStockBatch (int id);

	 String printBarcode(HttpServletRequest request,Integer id,boolean ifAll, String data);
	 
	 Map<String, Object> modifyNumber (String barcode,double nowqty);
}
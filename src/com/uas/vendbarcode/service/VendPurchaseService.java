package com.uas.vendbarcode.service;

import java.util.List;
import java.util.Map;

public interface VendPurchaseService {

	public Map<String, Object> getPurchaseList(String caller,String condition,Integer page,Integer start,Integer pageSize,Object vendcode);

	public Map<String, Object> getPurchaseForm(String caller,Integer id);
	
	public List<Map<String, Object>> getPurchaseGrid(String caller,Integer id);
}

package com.uas.mobile.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface DeviceService {

	Map<String, Object> getgetDeviceInfo (String decode);
	
	Map<String, Object> getCheckQty (String caller,Integer id);
	
	Map<String, Object> deviceStock (String caller,Integer id,String decode);
	
	Map<String,Object> saveAndSubmitDeviceStock(String caller,String formStore);
	
	Map<String,Object> saveAndSubmitDeviceChange(String caller,String formStore);
	
	Map<String,Object> updateAndSubmitDeviceChange(String caller,String formStore);
	
	void confirmDeal (String caller,Integer id);
	
	Map<String, Object> getDevInfomation (String decode);
	
	List<Map<String, Object>> getDevModelInfo (String centercode,String linecode,String workshop,String devmodel);

	void deviceInspectRes(String caller,int id);
	
	Integer turnScrap (int id,String caller);

	Map<String, Object> lossDevice (String caller, int id);
	
	Map<String, Object> getDeviceAttribute (String caller, int id);
}

package com.uas.pda.service;

import java.util.List;
import java.util.Map;

public interface PdaAcceptNotifyToVerifyService {

	Map<String, Object> getDataByBar (String bar_code);
	
	Map<String, Object> turnVerify (Integer an_id);
	
	List<Map<String, Object>> turnQC (Integer va_id);
	
	List<Map<String, Object>> getHaveList (String caller,String code,Integer page,Integer pageSize);
	
}

package com.uas.api.serve.service;

import java.util.List;
import java.util.Map;


public interface WisdomParkActivityService {
	
	List<Map<String, Object>> getActivityType(String basePath);
	
	Integer getActivityTotal(String type);
	
	List<Map<String, Object>> getActivitylist(String basePath, String type, Integer limit, Integer page);
	
	Map<String, Object> getActivityContent(String basePath, Integer id, Long uu);
	
	String ActivityRegistration(Integer id, Long uu, String name);

}
package com.uas.api.serve.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


public interface WisdomParkNewsService {
	
	List<Map<String, Object>> getNewsType(String basePath);
	
	Integer getNewsTotal(String type);
	
	List<Map<String, Object>> getNewslist(String basePath, String type, Integer limit, Integer page);
	
	Map<String, Object> getNewsContent(HttpServletRequest request, Integer id);

}
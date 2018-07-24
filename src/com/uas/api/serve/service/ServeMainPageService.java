package com.uas.api.serve.service;

import java.util.List;
import java.util.Map;

public interface ServeMainPageService {

	public Map<String, Object> getRecyclePics(String basePath, String kind);
	
	public List<Map<String, Object>> getServices(String basePath, String kind ,String type);

}

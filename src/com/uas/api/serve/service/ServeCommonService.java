package com.uas.api.serve.service;

import java.util.List;
import java.util.Map;

public interface ServeCommonService {
	
	public List<Map<String, Object>> getDefaultServices(String basePath, String kind);
	
	public void setDefaultServices(String kind, String ids);
	
	public List<Map<String, Object>> getProcesses(Long serve_id, Integer id);
	
}

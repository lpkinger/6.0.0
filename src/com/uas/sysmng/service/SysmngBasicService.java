package com.uas.sysmng.service;


import java.util.List;
import java.util.Map;
import java.util.List;
import java.util.Map;



public interface SysmngBasicService {

	public boolean checkModulePower(String emCode,String moduleCode);
	List<Map<String,Object>> getGrid1PanelByCaller(String caller);
	List<Map<String,Object>> getGrid2PanelByCaller(String caller);
	boolean saveGrid1PanelById(String addid,String deleteid);
	boolean saveGrid2PanelById(String addid,String deleteid);
	List<Map<String, Object>> getDictionaryData( String condition, int page, int pageSize,String tableName);
	
}

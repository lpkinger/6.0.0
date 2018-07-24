package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;

public interface SysnavigationService {
	
	List<JSONTree> getJSONTree();

	List<JSONTree> getJSONTreeByParentId(int parentId, String condition,Integer _noc);

	List<JSONTree> getJSONTreeBySearch(String search, Employee employee, Boolean isPower);
	
	List<JSONTree> getAllNavigation(int parentId, String condition);
	
	Map<String, Object> getNavigationDetails(int id);
	
	List<JSONTree> getJSONNavigationTreeBySearch(String search);
	
	void savePageinstruction(String caller,String field,String path,int id);
	
	void downloadPageinstruction(int id,String field);
	
	void initAllNavigation();
	
	Map<String, Object> getUpdatePath(int id,String num);
	
	Map<String, Object> getUpdateInfo(String num);
	
	String getUASNavigationPath(int id);
	
	String getNavigationPath(String num);
	
	void updateNavigation(int id,int addToId);
	
	Boolean checkUpgrade();
	
	public String refreshSysnavigation();
	
	List<CheckBoxTree> getAllCheckTree();
	
	Map<String, Object> updateAllNavigation();
	 
	List<Map<String, Object>> getAddBtn(); 

	List<JSONTree> getCommonUseTree(Employee employee);

	List<JSONTree> searchCommonUseTree(Employee employee, String value);
}

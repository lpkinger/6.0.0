package com.uas.erp.dao.common;

import java.util.List;
import java.util.Set;

import com.uas.erp.model.CheckBoxTree;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.SysNavigation;

public interface SysnavigationDao {
	
	List<SysNavigation> getSysNavigations();

	List<JSONTree> getJSONTreeByParentId(int parentId, String condition, Employee employee, Integer _noc);

	List<SysNavigation> getSysNavigationsByParentId(int parentId, String condition, Employee employee);

	Set<SysNavigation> getSysNavigationsBySearch(String search, Boolean isPower);
	
	List<JSONTree> getAllNavigation(int parentId, String condition);
	
	Set<SysNavigation> getNavigationTreeBySearch(String search);
	
	List<CheckBoxTree> getCheckTreeByParentId(int parentId);
}

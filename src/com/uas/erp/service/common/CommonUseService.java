package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.uas.erp.model.Employee;
import com.uas.erp.model.commonuse.CommonUseItem;

public interface CommonUseService {
	void importAll(Employee employee, JSONArray jsonstr);
	List<CommonUseItem> getList(Employee employee);
	void add(Employee employee, boolean group, String groupid, String itemid, String items, int index);
	void modify(Employee employee, String groupid, String text, int index);
	void remove(Employee employee, String id);
	Map<String, List<String>> synchronous(Employee employee, String[] sobs);
}

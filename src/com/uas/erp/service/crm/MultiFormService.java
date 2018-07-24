package com.uas.erp.service.crm;

import java.util.Map;

import com.uas.erp.model.Employee;



public interface MultiFormService {
	Map<String,Object> add(String form, String formDetail,String gridStore,
			String language, Employee employee,String type);
	void update(String form, String add, String update, String del, String language, Employee employee,String type);
	void updateDetailGrid(String add, String update, String del, String language, Employee employee);
	void mdelete(int id, String language, Employee employee,String type);
	void saveButtonGroup(String jsonstr,String caller);
	void deleteButtonGroup(String caller);
	void updateButton(String caller,String groupid,String oldText,String newText);
}

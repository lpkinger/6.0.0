package com.uas.erp.service.excel;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.model.excel.ExcelFileTemplate;

public interface ExcelFileTemplateService {
	
	int createTemplate (String filename,String desc,int subof,Boolean isCategory,Employee employee);

	void update(String filename, String desc, int id,String caller,Employee employee);
	
	void changeFileName(String id, String name, String description);

	ExcelFileTemplate getById(Integer fileId);
	
	ExcelFileTemplate getByCaller(String caller);
	

	List<JSONTree> getExcelTreeBySubof(int subof, String condition);

	void delete(int id, Boolean isCategory);

	int newFromTpl(String filecaller,Employee employee);

	Map<String, Object> getExcelInfo(int id);

}

package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.Employee;

public interface FilePathService {
	int saveFilePath(String path, int size, String fileName, Employee employee);

	String getFilepath(int id);

	List<String> getFilesPath(Object[] ids);
	
	List<Map<String,Object>> getData();
	
	List<Map<String,Object>> getDetailData(int id);
}

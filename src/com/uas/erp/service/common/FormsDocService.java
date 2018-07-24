package com.uas.erp.service.common;


import java.util.Map;



import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;

public interface FormsDocService {
	
	Map<String, Object> getFileList(String caller, Integer formsid, Integer id,Integer kind,Integer page,Integer start,
			Integer limit,String search);

	String upload(Employee employee, String caller, Integer fieldId, String condition, FileUpload uploadItem);

	Map<String, Object> saveAndUpdateTree(String caller, String create, String update);

	void deleteNode(String caller, String id);


}

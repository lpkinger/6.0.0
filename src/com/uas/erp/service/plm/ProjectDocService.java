package com.uas.erp.service.plm;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.uas.erp.model.Employee;
import com.uas.erp.model.FileUpload;

public interface ProjectDocService {
	
	List<Map<String, Object>> getProjectFileTree(String condition, String checked);

	Map<String,Object> getFileList(String formCondition,Integer id,Integer kind,Integer page,Integer start,Integer limit,Integer _noc,String search, boolean canRead);

	Map<String,Object> saveAndUpdateTree(String create,String update,Integer _noc);
	
	void deleteNode(String id,String type,Integer _noc);

	Map<String,Object> getProjectMsg(String formCondition);
	
	boolean ifMainTaskOpen(String condition);
	
	List<Map<String, Object>>getFilePowers(Integer docid,Integer prjid,Integer _noc);
	
	void saveFilePowers(Boolean appyforChilds,String filePowers,Integer _noc);
	
	String upload(Employee employee,Integer fieldId,String condition,Integer _noc,FileUpload uploadItem);

	HSSFWorkbook downloadAsExcel(String formCondition,String prids);

	
}

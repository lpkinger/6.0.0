package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.model.FileUploadTemp;

public interface FileUploadTempService {
	
	List<FileUploadTemp> getGridData(int id);
	
	void doMatchData(String datatype,int id);
	
	int putGridData(String data);
	
	Boolean uploadMulti(HttpSession session,MultipartFile file,
			String em_code,int id,String datatype);
	
	List<Map<String, Object>> uploadFiles(HttpSession session,MultipartFile[] files,String em_code,String caller);
	
	void updatefileUploadTemp(String update,String conditon);
	
	void updateDataAttach(String datatype,int code,String filename,Object matchcode);
	
	
	

}

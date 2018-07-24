package com.uas.erp.dao.common;

import java.util.List;

import com.uas.erp.model.FileUploadTemp;

public interface FileUploadTempDao {
	
	List<FileUploadTemp> getFileUploadById(Integer id);
	
}

package com.uas.erp.service.oa;

import java.io.File;

import com.uas.erp.model.Employee;

public interface FileUploadService {
	void fileUpload(File file, String language, Employee employee);
	
}

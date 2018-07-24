package com.uas.erp.service.excel;


import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.model.Employee;


public interface ExcelParseService {

	int parseExcel(MultipartFile file, int subof,Employee employee);
	
	
}

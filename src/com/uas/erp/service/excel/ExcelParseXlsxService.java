package com.uas.erp.service.excel;

import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.model.Employee;

public interface ExcelParseXlsxService {
	int parseExcelTemplate(MultipartFile file, int subof,Employee employee);

	int parseExcelFile(MultipartFile file, String filecaller, Employee employee);
}

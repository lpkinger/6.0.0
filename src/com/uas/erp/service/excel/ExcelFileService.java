package com.uas.erp.service.excel;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;

public interface ExcelFileService {
	
	ExcelFile getById(Integer fileId);

	void changeFileName(String id, String name, String description);

	Map<String, Object> getExcelInfo(int id);

	List<Map<String, Object>> getExcelsByTplsource(int filetplsource, int start, int end, String condition);

	void delete(int id);

	int getExcelCountByTplsource(int filetplsource, String condition);
	
}

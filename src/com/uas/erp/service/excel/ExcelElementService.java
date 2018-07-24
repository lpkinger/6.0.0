package com.uas.erp.service.excel;

import java.util.List;
import java.util.Map;

import com.uas.erp.model.excel.ExcelElement;
import com.uas.erp.model.excel.ExcelSheet;

public interface ExcelElementService {
	
	List<ExcelElement> findElementsBySheet(int sheetId);
	
	Map<String, Object> loadElementOnDemand (String fileId,String sheetId,Integer startElementId,Integer size);
	
	long getCountBySheetElement(Integer tabId);

	ExcelElement create(Map<String, Object> jsonObj, ExcelSheet sheetTab);

	void update(Map<String, Object> jsonObj, ExcelSheet sheetTab);

	void remove(Map<String, Object> jsonObj, ExcelSheet sheetTab);

	void createUpdate(Map<String, Object> jsonObj, ExcelSheet sheetTab);

	void batchInsert(List<ExcelElement> copiedElements);
	
}

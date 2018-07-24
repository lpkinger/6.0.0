package com.uas.erp.service.excel;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelFile;
import com.uas.erp.model.excel.ExcelFileTemplate;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.ExcelSheetSpan;
import com.uas.erp.model.excel.Triple;

public interface ExcelSheetService {
	
	ExcelSheet load(Integer sheetId);
	
	
	List<ExcelSheetSpan> getTabSpans(Set<Integer> keySet, String sheetcelltable_tpl);
	
	ExcelSheetSpan getSheetSpan(ExcelSheet sheet);
	
	
	List<ExcelSheet> getExcelSheetsByFileId(int fileid_tpl);
	
	Triple<Integer, Map<Integer, ExcelSheet>,  Map<Integer, ExcelSheetSpan>> getTabsWithSpan(Integer documentId);


	void activeTab(ExcelSheet tab);


	void renameSheet(String sheetId, String name);

	int createSheet(String fileId, Integer position, String name, String color);

	void deleteSheet(String sheetId);


	void copyFile(ExcelFileTemplate fileTemplate, ExcelFile file);


	List<ExcelCell> loadTabCornerCalCellOnDemand(ExcelSheet excelSheet, Integer startCellId, Integer size, Integer fileId, Boolean skipCal);


	void changeSheetOrder(String sheetId, Integer prePos, Integer curPos);


	Map<String, Object> copySheet(String oldSheetId, String newSheetName, Integer pos);


	
}

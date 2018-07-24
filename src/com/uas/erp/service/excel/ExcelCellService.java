package com.uas.erp.service.excel;

import java.util.Collection;
import java.util.List;

import com.uas.erp.model.excel.ExcelCell;
import com.uas.erp.model.excel.ExcelSheet;
import com.uas.erp.model.excel.SheetCellRangeCondition;

public interface ExcelCellService {
	
	void updateBatchCells(String actions,Boolean isTpl);
	
	List<ExcelCell> loadAllCellsByExcel(Collection<ExcelSheet> tabs, Integer startCellId, Integer size);

	List<ExcelCell> loadTabNonDataCellOfStyle(Integer sheetid_tpl, String sheetcelltable_tpl);

	Integer getTabCellCount(ExcelSheet excelSheetTemplate);

	void copySheetCellFromSheet(Integer getsheetid, Integer getsheetid2, String getsheetcelltable,
			String _cellTableName);

	List<ExcelCell> findCalCellsByTab(Integer integer, String _cellTableName);

	void updateContentOnly(Integer cellid, String content, String _cellTableName);

	List<Integer> getOtherTabIds(Integer integer, Integer activeTabId);

	List<ExcelCell> loadAllCellsByTabAndCalCells(ExcelSheet activeTab, Integer startCellId, Integer size,
			List<Integer> otherSheetIds);

	List<ExcelCell> getCornerCalCellsWithSize(ExcelSheet activeTab, Collection<ExcelSheet> values, Integer startCellId,
			Integer size);

	List<ExcelCell> loadTabCellOfRawDataOnDemand(ExcelSheet sheet, Integer startCellId, Integer size, Integer fileId,
			Boolean skipCal);

	List<ExcelCell> loadTabCellOnDemand(ExcelSheet sheet, Integer startCellId, Integer size, Integer fileId,
			Boolean skipCal);

	List<ExcelCell> getCellByRanges(List<SheetCellRangeCondition> rangeConditions, Integer nextCellId, Integer limit);

	
}

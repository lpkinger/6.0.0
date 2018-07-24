package com.uas.erp.service.excel;

import java.util.Map;

public interface ExcelCommonService {

	Map<String, Object> loadExcelInfo (Integer fileId, Integer startCellId, Integer size, int throttle, Boolean isTpl);
	
	Map<String, Object> loadExcelInfo5(Integer tabId, Boolean notActiveTabFlag, Integer throttleOfBigFile,
			Integer size);

	Map<String, Object> loadCellOnDemand3(Integer fileId, Integer sheetId, Integer startCellId, Integer size,
			Boolean skipCal);

	Map<String, Object> loadRange3(String range, Integer nextCellId, Integer limit, boolean b);
	
	
}

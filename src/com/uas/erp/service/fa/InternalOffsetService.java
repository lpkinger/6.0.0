package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface InternalOffsetService {

	List<Map<String, Object>> getInternalOffsets(int yearmonth);

	Map<String, Object> getInternalOffset(String fields, Integer yearmonth, String mastercode);

	String autoCatchInternalOffset(int yearmonth, String currency);

	void updateInternalOffset(String formStore, String gridStore);
	
	HSSFWorkbook exportMultitabExcel(String yearmonth);
	
	boolean valid(String yearmonth);

}

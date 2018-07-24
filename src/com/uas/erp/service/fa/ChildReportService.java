package com.uas.erp.service.fa;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

public interface ChildReportService {

	List<Map<String, Object>> getChildReports(int yearmonth, String fatype, String kind);

	Map<String, Object> getChildReport(String fields, Integer yearmonth, String mastercode, String fatype, String kind);

	String autoCatchReport(int yearmonth, String currency, String fatype, String kind);

	void updateChildReport(String formStore, String gridStore);

	String countConsolidated(int yearmonth, String currency, String fatype);

	HSSFWorkbook exportMultitabExcel(String yearmonth, String fatype, String kind);

	boolean valid(String yearmonth, String fatype, String kind);

	void saveReportYearBegin(String formStore, String gridStore, String caller);

	void updateReportYearBegin(String formStore, String gridStore, String caller);

}

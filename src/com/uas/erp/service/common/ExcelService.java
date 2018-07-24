package com.uas.erp.service.common;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

import com.uas.erp.model.ConditionItem;
import com.uas.erp.model.Employee;
public interface ExcelService {
	boolean getJsonData(Workbook wbs,String ExcelName,int fileId);
	void updateBatchCells(String tabId,String celljsons,Employee employee);
	boolean saveAsExcel(String name,Employee employee);
	boolean saveAsTemplate(String inJson, Employee employee);
	JSONObject getExcelTemplateByPage(int limit, int start, int count, String query, int enid, Employee employee);
	int getTemplateCount(String query, int enid, Employee employee);
	void getJsonDataByTemplate(int id,String colcondition, String cellcondition, String isTemplate,Employee employee);
	List<ConditionItem> getTemplateCondition(int id);
	boolean ishaveCondition(int id);
	Object downLoadAsExcel(String type,Employee employee);
	ByteArrayOutputStream  downLoadAsPDF(String title,Employee employee);
	String getResetData();
	void deleteTemplateById(int id);
	HSSFWorkbook savePanelAsExcel(String caller, int id,String mapstr, Employee employee, String language);
	String getFormTitle(String caller, int id);
	HSSFWorkbook twoExport(String data, String columns, Employee employee,String language);
	HSSFWorkbook exportBatchBOMAsExcel(Employee employee, String language);
	HSSFWorkbook exportBOMCheckMesExcel(String bomId, String caller);
	HSSFWorkbook saveTabPanelAsExcel(String sheetNames, Map<String, Object> grid, String gridTitle, String gridType);
}

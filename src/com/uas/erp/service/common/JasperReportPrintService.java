package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public interface JasperReportPrintService {
	Map<String, Object> print(int id,String caller, String reportname,boolean isProdIO, HttpServletRequest request);
	Map<String, Object> print(String param,String caller, String reportname, HttpServletRequest request);
	Map<String, Object> batchPrint(String ids,String caller,String reportname, HttpServletRequest request);
	Map<String, Object> getData( String condition, int page, int pageSize);
	int getCount(String condition);
	void save(String param);
	void delete(int id);
	public List<Map<String, Object>> getFields(String caller);
	public void setPrintType(HttpSession session);
	String JasperGetReportnameByProcedure(String ids,String caller,String reportname);
}

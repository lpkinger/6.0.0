package com.uas.erp.service.common;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public interface ReportService {

	/**
	 * 
	 * @param caller
	 *            界面caller
	 * @return 报表rpt文件路径
	 */
	String getReportPath(String caller);

	public List<Map<String, Object>> getDatasFields(String caller);

	/**
	 * 
	 * @param caller
	 *            界面caller
	 * @param reportName
	 *            报表名
	 * @return 报表rpt文件路径
	 */
	String getReportPath(String caller, String reportName);

	Map<String, Object> print(int id, String caller, String reportName, String condition, HttpServletRequest request);

	Map<String, Object> printMT(int id, String caller, String reportName, String condition, HttpServletRequest request);
	public Object[] getReportPathAndCondition(String caller) ;
}

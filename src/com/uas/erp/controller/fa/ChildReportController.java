package com.uas.erp.controller.fa;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.ChildReportService;

@Controller
public class ChildReportController extends BaseController {
	@Autowired
	private ChildReportService childReportService;

	/**
	 * 获取子公司设置
	 */
	@RequestMapping("/fa/gla/getChildReports.action")
	@ResponseBody
	public Map<String, Object> getChildReports(Integer yearmonth, String fatype, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("children", childReportService.getChildReports(yearmonth, fatype, kind));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取子报表form数据
	 */
	@RequestMapping("/fa/gla/getChildReport.action")
	@ResponseBody
	public Map<String, Object> getChildReport(String fields, Integer yearmonth, String mastercode, String fatype, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", childReportService.getChildReport(fields, yearmonth, mastercode, fatype, kind));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 子公司财报数据获取
	 */
	@RequestMapping("/fa/gla/autoCatchReport.action")
	@ResponseBody
	public Map<String, Object> autoCatchReport(int yearmonth, String currency, String fatype, String kind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		childReportService.autoCatchReport(yearmonth, currency, fatype, kind);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gla/updateChildReport.action")
	@ResponseBody
	public Map<String, Object> updateChildReport(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		childReportService.updateChildReport(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 集团合并报表计算
	 */
	@RequestMapping("/fa/gla/countConsolidated.action")
	@ResponseBody
	public Map<String, Object> countConsolidated(int yearmonth, String currency, String fatype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		childReportService.countConsolidated(yearmonth, currency, fatype);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 导出多Tab页的excel
	 * 
	 * @param response
	 * @param yearmonth
	 * @param fatype
	 * @param kind
	 * @throws IOException
	 */
	@RequestMapping("/fa/gla/exportMultitabExcel.action")
	@ResponseBody
	public void exportMultitabExcel(HttpServletResponse response, String yearmonth, String fatype, String kind) throws IOException {
		/*
		 * fatype = new String(fatype.getBytes("ISO-8859-1"),"utf-8"); kind =
		 * new String(kind.getBytes("ISO-8859-1"),"utf-8");
		 */
		HSSFWorkbook workbook = (HSSFWorkbook) childReportService.exportMultitabExcel(yearmonth, fatype, kind);
		if (workbook != null) {
			String filename = URLEncoder.encode(kind + ".xls", "UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			OutputStream out;
			out = response.getOutputStream();
			workbook.write(out);
			out.close();
		}
	}

	@RequestMapping("/fa/gla/childReportValid.action")
	@ResponseBody
	public String exportValid(String yearmonth, String fatype, String kind) throws IOException {
		boolean flag = childReportService.valid(yearmonth, fatype, kind);
		if (flag) {
			return "true";
		} else {
			return "false";
		}
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gla/saveReportYearBegin.action")
	@ResponseBody
	public Map<String, Object> saveReportYearBegin(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		childReportService.saveReportYearBegin(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gla/updateReportYearBegin.action")
	@ResponseBody
	public Map<String, Object> updateReportYearBegin(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		childReportService.updateReportYearBegin(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

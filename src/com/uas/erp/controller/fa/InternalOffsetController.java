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
import com.uas.erp.service.fa.InternalOffsetService;

@Controller
public class InternalOffsetController extends BaseController {
	@Autowired
	private InternalOffsetService internalOffsetService;

	/**
	 * 获取子公司设置
	 */
	@RequestMapping("/fa/gla/getInternalOffsets.action")
	@ResponseBody
	public Map<String, Object> getInternalOffset(Integer yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("children", internalOffsetService.getInternalOffsets(yearmonth));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取子账套合并抵消form数据
	 */
	@RequestMapping("/fa/gla/getInternalOffset.action")
	@ResponseBody
	public Map<String, Object> getInternalOffset(String fields, Integer yearmonth, String mastercode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", internalOffsetService.getInternalOffset(fields, yearmonth, mastercode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 子账套合并抵消数据获取
	 */
	@RequestMapping("/fa/gla/autoCatchInternalOffset.action")
	@ResponseBody
	public Map<String, Object> autoCatchReport(int yearmonth, String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		internalOffsetService.autoCatchInternalOffset(yearmonth, currency);
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
	@RequestMapping("/fa/gla/updateInternalOffset.action")
	@ResponseBody
	public Map<String, Object> updateInternalOffset(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		internalOffsetService.updateInternalOffset(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 导出多Tab页的excel
	 * @param response
	 * @param yearmonth
	 * @param fatype
	 * @param kind
	 * @throws IOException
	 */
	@RequestMapping("/fa/gla/InternalOffsetExportExcel.action")
	@ResponseBody
	public void exportMultitabExcel(HttpServletResponse response,String yearmonth) throws IOException{
		HSSFWorkbook workbook = (HSSFWorkbook) internalOffsetService.exportMultitabExcel(yearmonth);
		if(workbook != null){
			String filename = URLEncoder.encode("内部合并抵消数据" + ".xls", "UTF-8");
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition", "attachment;filename=" + filename);
			OutputStream out;
			out = response.getOutputStream();
			workbook.write(out);
			out.close();
		}
	}
	
	/**
	 * 验证是否存在ids（用于查找导出数据）
	 * @param yearmonth
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("/fa/gla/InternalOffsetValid.action")
	@ResponseBody
	public String exportValid(String yearmonth) throws IOException{
		boolean flag = internalOffsetService.valid(yearmonth);
		if(flag){
			return "true";
		}else{
			return "false";
		}
	}
}

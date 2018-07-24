package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
@Controller
public class ExcelFileController extends BaseController{

	@Autowired
	private ExcelFileService excelFileService;
	
	
	@RequestMapping("/Excel/file/getExcelsByTplsource.action")
	@ResponseBody
	public Map<String, Object> getExcelsByTplsource (
			@RequestParam(value="filetplsource",required=true)Integer filetplsource,
			String condition,
			int start,
			int limit,
			int page
			){
		Map<String, Object> modelMap = new HashMap<String,Object>();
		modelMap.put("success", true);
		modelMap.put("data", excelFileService.getExcelsByTplsource(filetplsource, start,limit*page,condition));
		modelMap.put("totalCount", excelFileService.getExcelCountByTplsource(filetplsource,condition));
		return modelMap;
	}
	
	@RequestMapping("/Excel/file/delete.action")
	@ResponseBody
	public Map<String, Object> delete (int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelFileService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}

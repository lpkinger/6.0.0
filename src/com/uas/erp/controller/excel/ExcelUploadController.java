package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.excel.ExcelCellService;
import com.uas.erp.service.excel.ExcelFileService;
import com.uas.erp.service.excel.ExcelFileTemplateService;
import com.uas.erp.service.excel.ExcelParseService;
import com.uas.erp.service.excel.ExcelParseXlsService;
import com.uas.erp.service.excel.ExcelParseXlsxService;
@Controller
public class ExcelUploadController extends BaseController{
	@Autowired
	private ExcelParseService parseService;
	
	@Autowired
	private ExcelParseXlsService parseXlsService;
	
	@Autowired
	private ExcelParseXlsxService parseXlsxService;
	
	/**导入解析ExcelTemplate
	 * @param fileId
	 * @param actions
	 * @param isTpl
	 * @return
	 */
	@RequestMapping(value="/Excel/upload/importExcelTemplate.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> importExcelTemplate(@RequestParam("file") MultipartFile file,int subof,
			HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String filename = file.getOriginalFilename();
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);
		Employee employee = (Employee)session.getAttribute("employee");
		if (suffix.toUpperCase().equals("XLS")) {
			modelMap.put("id", parseXlsService.parseExcelTemplate(file,subof,employee));
		}else if (suffix.toUpperCase().equals("XLSX")) {
			modelMap.put("id", parseXlsxService.parseExcelTemplate(file,subof,employee));
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value="/Excel/upload/importExcel.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file,String filecaller,
			HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String filename = file.getOriginalFilename();
		String suffix = filename.substring(filename.lastIndexOf('.') + 1);
		Employee employee = (Employee)session.getAttribute("employee");
		if (suffix.toUpperCase().equals("XLS")) {
			modelMap.put("id", parseXlsService.parseExcelFile(file, filecaller, employee));
		}else if (suffix.toUpperCase().equals("XLSX")) {
			modelMap.put("id", parseXlsxService.parseExcelFile(file, filecaller, employee));
		}
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
	
	
}

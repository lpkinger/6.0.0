package com.uas.erp.controller.excel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.model.JSONTree;
import com.uas.erp.service.excel.ExcelFileTemplateService;
/**
 * @author Administrator
 *
 */
@Controller
public class ExcelFileTemplateController extends BaseController{

	@Autowired
	private ExcelFileTemplateService excelFileTplService;
	
	
	/**创建模板
	 * @param filename
	 * @param desc
	 * @param subof
	 * @param isCategory
	 * @return
	 */
	@RequestMapping("/Excel/template/create.action")
	@ResponseBody
	public Map<String, Object> create (String filename,String desc,int subof,Boolean isCategory,
			HttpSession session){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("success", true);
		modelMap.put("id", excelFileTplService.createTemplate(filename, desc, subof, isCategory,employee));
		return modelMap;
	}

	@RequestMapping("/Excel/template/update.action")
	@ResponseBody
	public Map<String, Object> update (String filename,String desc,int id,String caller,
			HttpSession session){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		excelFileTplService.update(filename, desc, id,caller,employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
	
	
	@RequestMapping("/Excel/template/delete.action")
	@ResponseBody
	public Map<String, Object> delete (int id ,Boolean isCategory){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelFileTplService.delete(id,isCategory);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/Excel/template/getExcelTreeBySubof.action")
	@ResponseBody
	public Map<String, Object> getExcelTreeBySubof (int subof ,String condition){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<JSONTree> tree =excelFileTplService.getExcelTreeBySubof(subof, condition);
		modelMap.put("tree", tree);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	@RequestMapping("/Excel/template/newFromTpl.action")
	@ResponseBody
	public Map<String, Object> newFromTpl (String filecaller,HttpSession session){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("fileId", excelFileTplService.newFromTpl(filecaller,employee));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
}

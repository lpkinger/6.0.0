package com.uas.erp.controller.ma;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.ExportData;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.ma.ExportDataService;
@Controller
public class ExportDataController {
	@Autowired
	private ExportDataService exportDataService;
	@RequestMapping("/ma/testExportDataSet.action")  
	@ResponseBody 
	public Map<String, Object> test(String formStore,  String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean bool=exportDataService.testExportData(formStore);
		modelMap.put("success", bool);
		return modelMap;
	}
	@RequestMapping("/ma/saveExportDataSet.action")  
	@ResponseBody 
	public Map<String, Object> save( String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean bool=exportDataService.saveExportData(formStore);
		modelMap.put("success", bool);
		return modelMap;
	}
	@RequestMapping("/ma/deleteExportDataSet.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
	/*	String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");*/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		exportDataService.delteExportData(id);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/ma/updateExportDataSet.action")  
	@ResponseBody 
	public Map<String, Object> update( String formStore) {
		/*String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");*/
		Map<String, Object> modelMap = new HashMap<String, Object>();
		exportDataService.updateExportData(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/ma/getExportDetails.action")
	@ResponseBody
	public Map<String,Object> getExportDetails(int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();	
		GridPanel panel=exportDataService.getExportDetails(id);
		modelMap.put("success", true);
		modelMap.put("columns", panel.getGridColumns());
		modelMap.put("fields",panel.getGridFields());
		modelMap.put("data",panel.getDataString());
		return modelMap;
	}
	@RequestMapping("/ma/downloadAsExcel")
	@ResponseBody
	public void DownLoad(HttpServletResponse response, HttpServletRequest request,int id)throws IOException{
		ExportData exportdata=exportDataService.downLoadAsExcel(id);
		HSSFWorkbook workbook=exportdata.getWook();
		response.setContentType("application/vnd.ms-excel"); 
		String filename=URLEncoder.encode(exportdata.getEd_name()+".xls", "UTF-8");
		response.setHeader("Content-Disposition", "attachment;filename=" + filename);
		OutputStream out;
		out = response.getOutputStream();
		workbook.write(out);
		out.close();  
	}
}

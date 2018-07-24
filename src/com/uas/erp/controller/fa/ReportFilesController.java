package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReportFilesService;

@Controller
public class ReportFilesController {
	@Autowired
	private ReportFilesService ReportFilesService;
	/**
	 * 保存ReportFiles
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveReportFiles.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesService.saveReportFiles(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/fa/fp/updateReportFiles.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesService.updateReportFiles(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ReportFiles
	 */
	@RequestMapping("/fa/fp/deleteReportFiles.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesService.deleteReportFiles(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * grid模式保存
	 * @param caller
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/fp/saveReportFilesG.action")
	@ResponseBody 
	public Map<String, Object> saveLPrintSetting(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesService.saveReportFilesG(caller,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * grid模式删除
	 * @param caller
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/fp/deleteReportFilesG.action")  
	@ResponseBody 
	public Map<String, Object> deleteReportFiles(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReportFilesService.deleteReportFilesG(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

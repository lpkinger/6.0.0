package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.AqlService;
@Controller
public class AqlController {
	@Autowired
	AqlService aqlService;
	
	
	@RequestMapping("/scm/qc/saveAql.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();	
		aqlService.saveAql(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/qc/deleteAql.action")  
	@ResponseBody 
	public Map<String, Object> deleteAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.deleteAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/qc/updateAql.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.updateAqlById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printAql.action")  
	@ResponseBody 
	public Map<String, Object> printAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.printAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditAql.action")  
	@ResponseBody 
	public Map<String, Object> auditAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.auditAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditAql.action")  
	@ResponseBody 
	public Map<String, Object> resAuditAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.resAuditAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitAql.action")  
	@ResponseBody 
	public Map<String, Object> submitAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.submitAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/resSubmitAql.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitAql(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		aqlService.resSubmitAql(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}

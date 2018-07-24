package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.AssessSchemeService;



@Controller
public class AssessSchemeController {
	@Autowired
	private AssessSchemeService assessSchemeService;
	
	/**
	 * 保存
	 */
	@RequestMapping(value = "/fs/credit/saveAssessScheme.action")
	@ResponseBody
	public Map<String,Object> saveAssessScheme(String formStore,String param,String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		assessSchemeService.saveAssessScheme(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新
	 */
	@RequestMapping(value = "/fs/credit/updateAssessScheme.action")
	@ResponseBody
	public Map<String,Object> updateAssessScheme(String formStore,String param,String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		assessSchemeService.updateAssessScheme(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/fs/credit/deleteAssessScheme.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assessSchemeService.deleteAssessScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/fs/credit/submitAssessScheme.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assessSchemeService.submitAssessScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fs/credit/resSubmitAssessScheme.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assessSchemeService.resSubmitAssessScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/fs/credit/auditAssessScheme.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		assessSchemeService.auditAssessScheme(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/fs/credit/resAuditAssessScheme.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assessSchemeService.resAuditAssessScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustPersonInfoService;

@Controller
public class CustPersonInfoController {
	
	@Autowired
	private CustPersonInfoService custPersonInfoService;
	
	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveCustPersonInfo.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.saveCustPersonInfo(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateCustPersonInfo.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.updateCustPersonInfo(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteCustPersonInfo.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.deleteCustPersonInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitCustPersonInfo.action")
	@ResponseBody
	public Map<String, Object> submitCustPersonInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.submitCustPersonInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitCustPersonInfo.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustPersonInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.resSubmitCustPersonInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditCustPersonInfo.action")  
	@ResponseBody 
	public Map<String, Object> auditCustPersonInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.auditCustPersonInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditCustPersonInfo.action")  
	@ResponseBody 
	public Map<String, Object> resAuditCustPersonInfo(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custPersonInfoService.resAuditCustPersonInfo(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.MulticolacSchemeService;



@Controller
public class MulticolacSchemeController {
	@Autowired
	private MulticolacSchemeService multicolacSchemeService;
	
	/**
	 * 保存
	 */
	@RequestMapping(value = "/fa/gla/saveMulticolacScheme.action")
	@ResponseBody
	public Map<String,Object> saveMulticolacScheme(String formStore,String param,String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		multicolacSchemeService.saveMulticolacScheme(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新
	 */
	@RequestMapping(value = "/fa/gla/updateMulticolacScheme.action")
	@ResponseBody
	public Map<String,Object> updateMulticolacScheme(String formStore,String param,String param2,String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		multicolacSchemeService.updateMulticolacScheme(formStore,param,param2,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/fa/gla/deleteMulticolacScheme.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		multicolacSchemeService.deleteMulticolacScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/fa/gla/submitMulticolacScheme.action")
	@ResponseBody
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		multicolacSchemeService.submitMulticolacScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gla/resSubmitMulticolacScheme.action")
	@ResponseBody
	public Map<String, Object> resSubmit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		multicolacSchemeService.resSubmitMulticolacScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/fa/gla/auditMulticolacScheme.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		multicolacSchemeService.auditMulticolacScheme(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gla/resAuditMulticolacScheme.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		multicolacSchemeService.resAuditMulticolacScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 自动编排
	 */
	@RequestMapping(value = "/fa/gla/autoArrange.action")
	@ResponseBody
	public Map<String,Object> autoArrange(String formStore, String caller) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap.put("result", multicolacSchemeService.autoArrange(formStore,caller));
		modelMap.put("success", true);
		return modelMap;
	}
}

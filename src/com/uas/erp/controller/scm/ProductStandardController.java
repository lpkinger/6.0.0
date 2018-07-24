package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductStandardService;

@Controller
public class ProductStandardController {
	@Autowired
	ProductStandardService productStandardService;
	
	@RequestMapping("/scm/product/saveProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> saveProductStandard(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.saveProductStandard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/deleteProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> deleteProductStandard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.deleteProductStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/updateProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> updateProductStandard(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.updateProductStandardById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/submitProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> submitProductStandard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.submitProductStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/resSubmitProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductStandard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.resSubmitProductStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/auditProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> auditProductStandard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.auditProductStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/resAuditProductStandard.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductStandard(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productStandardService.resAuditProductStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductSampleService;

@Controller
public class ProductSampleController {
	
	@Autowired
	private ProductSampleService productSampleService;
	/**
	 * 保存ProductSample
	 */
	@RequestMapping("/scm/product/saveProductSample.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.saveProductSample(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductSample.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.updateProductSampleById(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductSample.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.deleteProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductSample.action")  
	@ResponseBody 
	public Map<String, Object> submitProductSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.submitProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductSample.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.resSubmitProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductSample.action")  
	@ResponseBody 
	public Map<String, Object> auditProductSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.auditProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductSample.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductSample(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.resAuditProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 作废
	 */
	@RequestMapping("/scm/product/nullifyProductSample.action")
	@ResponseBody
	public Map<String, Object> nullifyProductSample(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSampleService.nullifyProductSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

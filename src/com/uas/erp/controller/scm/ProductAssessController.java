package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductAssessService;

@Controller
public class ProductAssessController {
	@Autowired
	private ProductAssessService ProductAssessService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/product/saveProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,  String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.saveProductAssess( formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除ECN数据
	 * 包括ECN明细
	 */
	@RequestMapping("/scm/product/deleteProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> deleteProductAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.deleteProductAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/product/updateProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.updateProductAssess(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> submitProductAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.submitProductAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.resSubmitProductAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> auditProductAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.auditProductAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductAssess.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProductAssessService.resAuditProductAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

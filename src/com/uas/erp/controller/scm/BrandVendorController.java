package com.uas.erp.controller.scm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BrandVendorService;
@Controller
public class BrandVendorController {
	@Autowired
	private BrandVendorService brandVendorService;
	/**
	 * 保存
	 */
	@RequestMapping("/scm/sale/saveBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.saveBrandVendor(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.updateBrandVendor(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.deleteBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> submitProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.submitBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.resSubmitBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> auditProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.auditBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.resAuditBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/sale/bannedBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> bannedProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.bannedBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/sale/resBannedBrandVendor.action")  
	@ResponseBody 
	public Map<String, Object> resBannedProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		brandVendorService.resBannedBrandVendor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

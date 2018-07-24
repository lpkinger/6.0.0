package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VendorBannedApplyService;

@Controller
public class VendorBannedApplyController {
	@Autowired
	private VendorBannedApplyService vendorBannedApplyService;
	/**
	 * 保存VendorBannedApply
	 */
	@RequestMapping("/scm/purchase/saveVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.saveVendorBannedApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/purchase/updateVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.updateVendorBannedApplyById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.deleteVendorBannedApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核VendorBannedApply
	 */
	@RequestMapping("/scm/purchase/auditVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.auditVendorBannedApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核VendorBannedApply
	 */
	@RequestMapping("/scm/purchase/resAuditVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.resAuditVendorBannedApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交VendorBannedApply
	 */
	@RequestMapping("/scm/purchase/submitVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.submitVendorBannedApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交VendorBannedApply
	 */
	@RequestMapping("/scm/purchase/resSubmitVendorBannedApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorBannedApplyService.resSubmitVendorBannedApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

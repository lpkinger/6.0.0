package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.SupplierAssessService;

@Controller
public class SupplierAssessController {
	@Autowired
	private SupplierAssessService supplierAssessService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,  String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.saveSupplierAssess( formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除ECN数据
	 * 包括ECN明细
	 */
	@RequestMapping("/scm/purchase/deleteSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> deleteSupplierAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.deleteSupplierAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.updateSupplierAssess(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> submitSupplierAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.submitSupplierAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSupplierAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.resSubmitSupplierAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> auditSupplierAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.auditSupplierAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditSupplierAssess.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSupplierAssess(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		supplierAssessService.resAuditSupplierAssess(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

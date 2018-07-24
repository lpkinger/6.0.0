package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductApprovalService;

@Controller
public class ProductApprovalController {
	
	@Autowired
	private ProductApprovalService productApprovalService;
	
	/**
	 * 保存ProductApproval
	 * @param formStore form数据
	 * @param param 其它数据
	 *//*
	@RequestMapping("/scm/product/saveProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param1,String param2,String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveProductApproval(formStore,param1,param2,param3, caller);
		modelMap.put("success", true);
		return modelMap;
	}*/
	@RequestMapping("/scm/product/saveproductApprovalDetail.action")  
	@ResponseBody 
	public Map<String, Object> saveproductApprovalDetail(String caller, String formStore, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveproductApprovalDetail(formStore,gridStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/product/saveprodApprovalDetail.action")  
	@ResponseBody 
	public Map<String, Object> saveprodApprovalDetail(String caller, String formStore, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveprodApprovalDetail(formStore,gridStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/product/saveprodAppDetail.action")  
	@ResponseBody 
	public Map<String, Object> saveprodAppDetail(String caller, String formStore, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveprodAppDetail(formStore,gridStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/scm/product/saveProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> saveProductApproval(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveProductApproval(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.updateProductApprovalById(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存认定结果
	 */
	@RequestMapping("/scm/product/saveApprovalResult.action")  
	@ResponseBody 
	public Map<String, Object> saveFinalResult(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.saveApprovalResult(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.deleteProductApproval(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> submitProductApproval(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.submitProductApproval(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductApproval(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.resSubmitProductApproval(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> auditProductApproval(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.auditProductApproval(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductApproval.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductApproval(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productApprovalService.resAuditProductApproval(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

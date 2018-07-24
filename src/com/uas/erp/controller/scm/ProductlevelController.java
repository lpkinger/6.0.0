package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductlevelService;

@Controller
public class ProductlevelController {
	
	@Autowired
	private ProductlevelService productlevelService;
	/**
	 * 保存Productlevel
	 */
	@RequestMapping("/scm/product/saveProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.saveProductlevel(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore,String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.updateProductlevelById(formStore,param,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.deleteProductlevel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> submitProductlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.submitProductlevel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.resSubmitProductlevel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> auditProductlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.auditProductlevel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductlevel.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductlevel(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.resAuditProductlevel(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 
	 * @param session
	 * @param id
	 * @param param
	 * @return
	 */
	@RequestMapping("/scm/product/updatePurchasetypedetail.action")  
	@ResponseBody
	public Map<String, Object> updatePurchasetypedetail(String caller, int id,String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productlevelService.updatePurchasetypedetail(id, param, caller);
		modelMap.put("success", true);
		return modelMap;
		
	}
}

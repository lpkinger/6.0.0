package com.uas.erp.controller.scm;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.ProductBrandService;
@Controller
public class ProductBrandController {
	@Autowired
	private ProductBrandService productBrandService;
	/**
	 * 保存
	 */
	@RequestMapping("/scm/product/saveProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.saveProductBrand(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.updateProductBrandById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.deleteProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> submitProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.submitProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.resSubmitProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> auditProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.auditProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.resAuditProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/product/bannedProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> bannedProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.bannedProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/product/resBannedProductBrand.action")  
	@ResponseBody 
	public Map<String, Object> resBannedProductBrand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productBrandService.resBannedProductBrand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductVendorService;

@Controller
public class ProductVendorController {
	@Autowired
	private ProductVendorService productVendorService;	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/purchase/updateProductVendor.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productVendorService.updateProductVendorById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 载入物料供应商
	 */
	@RequestMapping("/scm/purchase/loadProductVendor.action")  
	@ResponseBody 
	public Map<String, Object> load( String prodcode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data",productVendorService.loadProductVendor(prodcode));
		modelMap.put("success", true);
		return modelMap;
	}	
	
	
	/**
	 *保存物料供应商分配
	 */
	@RequestMapping("/scm/purchase/updateProductVendorRate.action")  
	@ResponseBody 
	public Map<String, Object> updateVenderRate( String param,String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		productVendorService.updateVendorRate(param,formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
}

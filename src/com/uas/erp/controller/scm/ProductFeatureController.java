package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductFeatureService;

@Controller
public class ProductFeatureController {
	@Autowired
	private ProductFeatureService productFeatureService;
	/**
	 * 保存product!feature
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveProductFeature.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productFeatureService.saveProductFeature(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/product/updateProductFeature.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productFeatureService.updateProductFeatureById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}

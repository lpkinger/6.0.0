package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductMatchService;

@Controller
public class ProductMatchController {
	@Autowired
	private ProductMatchService productMatchService;
	/**
	 * 保存ProductMatch
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/product/saveProductMatch.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productMatchService.saveProductMatch(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteProductMatch.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productMatchService.deleteProductMatch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/product/updateProductMatch.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productMatchService.updateProductMatchById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

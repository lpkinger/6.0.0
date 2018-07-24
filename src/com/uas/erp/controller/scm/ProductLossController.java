package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductLossService;

@Controller
public class ProductLossController {
	@Autowired
	private ProductLossService productLossService;
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/saveProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.saveProductLoss(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.deleteProductLoss(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/updateProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.updateProductLoss(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

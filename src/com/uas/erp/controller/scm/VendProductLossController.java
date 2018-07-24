package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.VendProductLossService;

/**
 * 
 * @author zjh
 *
 */
@Controller
public class VendProductLossController {
	@Autowired
	private VendProductLossService productLossService;
	
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/product/saveVendProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.saveVendProductLoss(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/product/deleteVendProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.deleteVendProductLoss(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/product/updateVendProductLoss.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossService.updateVendProductLoss(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

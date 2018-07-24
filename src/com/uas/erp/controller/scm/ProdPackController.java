package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProdPackService;

@Controller
public class ProdPackController {
	@Autowired
	private ProdPackService prodPackService;
	/**
	 * 保存ProdPack
	 */
	@RequestMapping("/scm/sale/saveProdPack.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodPackService.saveProdPack(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateProdPack.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodPackService.updateProdPackById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteProdPack.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodPackService.deleteProdPack(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

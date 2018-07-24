package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.ma.MADocumentSetupService;

@Controller
public class MADocumentSetupController {
	
	@Autowired
	private MADocumentSetupService maDocumentSetupService;
	/**
	 * @param formStore form数据
	 */
	@RequestMapping("/ma/saveDocumentSetup.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDocumentSetupService.save(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/ma/deleteDocumentSetup.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDocumentSetupService.delete(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * @param formStore form数据
	 */
	@RequestMapping("/ma/updateDocumentSetup.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		maDocumentSetupService.update(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}

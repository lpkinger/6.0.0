package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.MadeinSetupService;

@Controller
public class MadeinSetupController {
	@Autowired
	private MadeinSetupService madeinSetupService;
	/**
	 * 保存MadeinSetup
	 */
	@RequestMapping("/scm/sale/saveMadeinSetup.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		madeinSetupService.saveMadeinSetup(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateMadeinSetup.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		madeinSetupService.updateMadeinSetupById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteMadeinSetup.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		madeinSetupService.deleteMadeinSetup(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

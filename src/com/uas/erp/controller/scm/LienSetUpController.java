package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.LienSetUpService;

@Controller
public class LienSetUpController {
	@Autowired
	private LienSetUpService lienSetUpService;
	/**
	 * 保存LienSetUp
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/sale/saveLienSetUp.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lienSetUpService.saveLienSetUp(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateLienSetUp.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lienSetUpService.updateLienSetUpById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteLienSetUp.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lienSetUpService.deleteLienSetUp(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

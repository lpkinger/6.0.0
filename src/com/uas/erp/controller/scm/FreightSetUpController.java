package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.FreightSetUpService;

@Controller
public class FreightSetUpController {
	@Autowired
	private FreightSetUpService freightSetUpService;
	/**
	 * 保存FreightSetUp
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/scm/sale/saveFreightSetUp.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		freightSetUpService.saveFreightSetUp(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateFreightSetUp.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		freightSetUpService.updateFreightSetUpById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteFreightSetUp.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		freightSetUpService.deleteFreightSetUp(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

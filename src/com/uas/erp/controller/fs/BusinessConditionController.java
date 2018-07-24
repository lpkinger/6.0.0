package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.BusinessConditionService;

@Controller
public class BusinessConditionController {

	@Autowired
	private BusinessConditionService businessConditionService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveBusinessCondition.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		businessConditionService.saveBusinessCondition(formStore, caller, param1, param2, param3, param4, param5);
		modelMap.put("success", true);
		return modelMap;
	}

}

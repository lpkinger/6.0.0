package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductCheckConditionService;

@Controller
public class ProductCheckConditionController {
	@Autowired
	private ProductCheckConditionService productCheckConditionService;
	
	@RequestMapping("scm/qc/saveProductCheckCondition.action")
	@ResponseBody
	public Map<String,Object> updateAccountproductCheckCondition(String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productCheckConditionService.saveProductCheckConditionById(null, param);
		modelMap.put("success", true);
		return modelMap;
	}
}

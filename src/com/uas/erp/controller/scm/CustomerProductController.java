package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.CustomerProductService;

@Controller
public class CustomerProductController {
	@Autowired
	private CustomerProductService customerProductService;
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateCustomerProduct.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerProductService.updateCustomerProductById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
}
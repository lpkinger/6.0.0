package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductCustomerService;

@Controller
public class ProductCustomerController {
	@Autowired
	private ProductCustomerService productCustomerService;
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateProductCustomer.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productCustomerService.updateProductCustomerById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}

package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustomerPayTaxesService;

@Controller
public class CustomerPayTaxesController {
	@Autowired
	private CustomerPayTaxesService customerPayTaxesService;
	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateCustomerPayTaxes.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerPayTaxesService.updateCustomerPayTaxesById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}
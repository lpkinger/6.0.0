package com.uas.erp.controller.b2b;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2b.ProductSampleDownService;

@Controller
public class ProductSampleDownController {
	@Autowired
	private ProductSampleDownService productSampleDownService;
	
	
	/**
	 * 转送样
	 */
	@RequestMapping("/b2b/product/turnCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> turnCustSendSample(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int ssid=productSampleDownService.turnCustSendSample(id);
		modelMap.put("id", ssid);
		modelMap.put("success", true);
		return modelMap;
		
	}
	
}

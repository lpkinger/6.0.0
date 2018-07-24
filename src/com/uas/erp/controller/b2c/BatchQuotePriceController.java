package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServlet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2c.BatchQuotePriceService;

@Controller
public class BatchQuotePriceController {
    @Autowired
    private BatchQuotePriceService batchQuotePriceService;
	
	@RequestMapping(value = "/b2c/getCurrencyAndTaxrate.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getCurrencyAndTaxrate(String caller,String code) {
		return batchQuotePriceService.getCurrencyAndTaxrate(caller,code);
	}
    
	@RequestMapping("/b2c/batchquoteprice.action")
	@ResponseBody
	public Map<String,Object> quotePrice(String caller,String parameters){
		Map<String,Object> modelMap = new HashMap<String,Object>();
			modelMap = batchQuotePriceService.quotePrice(caller, parameters);
			modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.VendorProductService;

@Controller
public class VendorProductController {
	@Autowired
	private  VendorProductService vendorProductService;	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/purchase/updateVendorProduct.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorProductService.updateVendorProductById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}

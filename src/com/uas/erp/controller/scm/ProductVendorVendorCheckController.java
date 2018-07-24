package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductVendorCheckService;

@Controller
public class ProductVendorVendorCheckController {
	@Autowired
	private ProductVendorCheckService productVendorCheckService;
	
	@RequestMapping("scm/qc/saveProductVendorCheckCondition.action")
	@ResponseBody
	public Map<String,Object> updateAccountProductVendorCheck(String caller, String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productVendorCheckService.saveProductVendorCheckById(null, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

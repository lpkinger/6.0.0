package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ProductReserveCostService;

@Controller
public class ProductReserveCostController {
	@Autowired
	private ProductReserveCostService productReserveCostService;
	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/reserve/updateProductReserveCost.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReserveCostService.updateProductWHById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
}

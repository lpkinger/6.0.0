package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.scm.SaleDetailDetService;

@Controller
public class SaleDetailDetController {
	
	@Autowired
	private SaleDetailDetService saleDetailDetService;
	
	@RequestMapping("/scm/sale/updateSaleDetailSet.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDetailDetService.updateSaleDetailSet(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	
	@RequestMapping("/scm/sale/checkSaleDetailDet.action")  
	@ResponseBody 
	public Map<String, Object> checkSaleDetailSet(Integer sd_id, Integer 
			sd_qty,Integer sd_yqty, String sd_delivery, String whereString) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleDetailDetService.SetSaleDelivery(whereString);
		modelMap.put("success", true);
		return modelMap;
	}

}

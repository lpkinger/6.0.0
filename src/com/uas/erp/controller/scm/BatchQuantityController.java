package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BatchQuantityService;

@Controller
public class BatchQuantityController {
	@Autowired
	private BatchQuantityService batchQuantityService;
	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/reserve/updateBatchQuantity.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchQuantityService.updateBatchById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	
}

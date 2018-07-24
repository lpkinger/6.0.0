package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BatchMoneyService;

@Controller
public class BatchMoneyController {
	@Autowired
	private BatchMoneyService batchMoneyService;
	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/reserve/updateBatchMoney.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchMoneyService.updateBatchById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	
}

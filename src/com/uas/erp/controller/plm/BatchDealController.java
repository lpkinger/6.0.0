package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.plm.BatchDealService;

@Controller("PlmBatchDealController")
public class BatchDealController {
	
	@Autowired
	private BatchDealService batchDealService;
	
	@RequestMapping("plm/makeDeal.action")  
	@ResponseBody 
	public Map<String, Object> makeDeal(String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", batchDealService.makeDeal(data));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 试产订单批量结案
	 * @param session
	 * @param caller
	 * @param data
	 * @return
	 */
	@RequestMapping(value="/plm/SalevastClose.action")
	@ResponseBody
	public Map<String, Object> salevastColse(String data){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		batchDealService.salevastClose(data);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  批量测试BUG
	 */
	@RequestMapping(value = "/plm/batchTestBug.action")
	@ResponseBody
	public Map<String, Object> batchTestBug(String caller,String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = batchDealService.batchTestBug(caller,data);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
	/**
	 * 
	 */
}

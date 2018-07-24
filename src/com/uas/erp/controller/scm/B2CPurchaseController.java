package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.B2CPurchaseService;

@Controller
public class B2CPurchaseController {

	@Autowired
	private B2CPurchaseService B2CPurchaseService;
	/**
	 * 请购转采购平台购买，根据物料编号获取最新的库存数据
	 * @param pr_code
	 * @return
	 */
	@RequestMapping(value = "/scm/turnPurchase/getReserveByUUid.action")
	@ResponseBody 
	public Map<String, Object> getReserveByUUid(String pr_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("data",B2CPurchaseService.getReserveByUUid(pr_code));
		return modelMap;
	}
	
	
	/**
	 * 确认平台购买
	 * @param pr_code
	 * @return
	 */
	@RequestMapping(value = "/scm/turnPurchase/comfirmB2CPurchase.action")
	@ResponseBody 
	public Map<String, Object> comfirmB2CPurchase(String param,String data,String caller,String currency) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log",B2CPurchaseService.comfirmB2CPurchase(param,data,caller,currency));
		return modelMap;
	}
}

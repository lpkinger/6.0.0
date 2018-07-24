package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2c.B2CSettingService;

@Controller
public class B2CSettingController {
  @Autowired
  private B2CSettingService b2CSettingService;
  
    /**
     * B2C 商城参数设置，获取参数设置
	 */
	@RequestMapping("/b2c/getB2CSetting.action")
	@ResponseBody
	public Map<String, Object> getB2CSetting(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",b2CSettingService.getB2CSetting(caller));
		modelMap.put("success", true);
		return modelMap;
	}
    /**
     * B2C 商城参数设置，配置商城客户
	 */
	@RequestMapping("/b2c/saveB2CCustomer.action")
	@ResponseBody
	public Map<String, Object> saveB2CCustomer(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		b2CSettingService.saveB2CCustomer(param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
     * B2C 商城参数设置，配置商城供应商
	 */
	@RequestMapping("/b2c/saveB2CVendor.action")
	@ResponseBody
	public Map<String, Object> saveB2CVendor(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		b2CSettingService.saveB2CVendor(param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
     * B2C 商城参数设置，配置销售类型
	 */
	@RequestMapping("/b2c/saveB2CSaleKind.action")
	@ResponseBody
	public Map<String, Object> saveB2CSaleKind(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		b2CSettingService.saveB2CSaleKind(param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
     * B2C 商城参数设置，启用检测
	 */
	@RequestMapping("/b2c/startB2C.action")
	@ResponseBody
	public Map<String, Object> startB2C(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		b2CSettingService.startB2C(caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.EncryptService;

@Controller
public class EncryptController {

	@Autowired
	private EncryptService encryptService;
	
	@RequestMapping("/ma/encrypt/getConfigs.action")
	@ResponseBody
	public Map<String,Object> getSob(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap = encryptService.getSob();
		return modelMap;
	}
	
	@RequestMapping("/ma/encrypt/updateConfigs.action")
	@ResponseBody
	public Map<String,Object> updateConfigs(String value) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		encryptService.updateConfigs(value);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

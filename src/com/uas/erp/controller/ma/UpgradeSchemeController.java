package com.uas.erp.controller.ma;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.UpgradeSchemeService;

@Controller
public class UpgradeSchemeController {
	@Autowired
	private UpgradeSchemeService upgradeSchemeService;

	/**
	 * 检测
	 */
	@RequestMapping("/ma/upgrade/check.action")
	@ResponseBody
	public Map<String, Object> check(String ids) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("result",upgradeSchemeService.check(ids));
		return modelMap;
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/ma/upgrade/saveUpgradeScheme.action")
	@ResponseBody
	public Map<String, Object> saveUpgradeScheme(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		upgradeSchemeService.saveUpgradeScheme(param);
		modelMap.put("success", true);
		return modelMap;
	}
}

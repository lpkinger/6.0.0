package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.FeederUseService;

@Controller
public class FeederUseController extends BaseController {
	@Autowired
	private FeederUseService feederUseService;

	@RequestMapping("/pm/mes/getFeeder.action")
	@ResponseBody
	public Map<String, Object> save(String feedercode, String makecode, String linecode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederUseService.getFeeder(feedercode, makecode, linecode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/returnFeeder.action")
	@ResponseBody
	public Map<String, Object> deleteAppMould(String feedercode,String reason, int isuse) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederUseService.returnFeeder(feedercode, reason, isuse);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/mes/returnAllFeeder.action")
	@ResponseBody
	public Map<String, Object> update(String makecode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		feederUseService.returnAllFeeder(makecode);
		modelMap.put("success", true);
		return modelMap;
	}
}

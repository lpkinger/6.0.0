package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.AssetsLiabilitiesService;

@Controller
public class AssetsLiabilitiesController {

	@Autowired
	private AssetsLiabilitiesService assetsLiabilitiesService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveAssetsLiabilities.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param1, String param2, String param3, String param4,
			String param5, String param6, String param7, String param8, String param9, String param10) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsLiabilitiesService.saveAssetsLiabilities(formStore, caller, param1, param2, param3, param4, param5, param6, param7, param8,
				param9, param10);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveAccountInforDet.action")
	@ResponseBody
	public Map<String, Object> saveAccountInforDet(String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsLiabilitiesService.saveAccountInforDet(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveFinancCondition.action")
	@ResponseBody
	public Map<String, Object> saveFinancCondition(String formStore, String caller, String param1, String param2, String param3,
			String param4, String param5, String param6, String param7, String param8, String param9, String param10, String param11) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		assetsLiabilitiesService.saveFinancCondition(formStore, caller, param1, param2, param3, param4, param5, param6, param7, param8,
				param9, param10, param11);
		modelMap.put("success", true);
		return modelMap;
	}
}

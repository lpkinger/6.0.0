package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.oa.EmFoodCardService;

@Controller
public class EmFoodCardController extends BaseController {
	@Autowired
	private EmFoodCardService emFoodCardService;

	@RequestMapping("/oa/emFoodCard/saveEmFoodCard.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		emFoodCardService.saveEmFoodCard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/oa/emFoodCard/deleteEmFoodCard.action")
	@ResponseBody
	public Map<String, Object> deleteMarketProject(String caller, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
		emFoodCardService.deleteEmFoodCard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/oa/emFoodCard/updateEmFoodCard.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		emFoodCardService.updateEmFoodCard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.ma.RelativeSearchService;

@Controller
public class RelativeSearchController {

	@Autowired
	private RelativeSearchService relativeSearchService;

	@RequestMapping("/ma/saveRelativeSearch.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2 };
		relativeSearchService.saveRelativeSearch(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/ma/updateRelativeSearch.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] params = new String[] { param, param2 };
		relativeSearchService.updateRelativeSearchById(formStore, params, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/ma/deleteRelativeSearch.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		relativeSearchService.deleteRelativeSearch(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

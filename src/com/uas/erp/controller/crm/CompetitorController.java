package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.CompetitorService;

@Controller
public class CompetitorController {
	@Autowired
	private CompetitorService competitorService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/marketCompete/saveCompetitor.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		competitorService.saveCompetitor(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/marketCompete/deleteCompetitor.action")
	@ResponseBody
	public Map<String, Object> deleteCompetitor(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		competitorService.deleteCompetitor(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/crm/marketCompete/updateCompetitor.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		competitorService.updateCompetitorById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

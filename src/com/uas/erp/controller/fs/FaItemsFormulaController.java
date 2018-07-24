package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.FaItemsFormulaService;

@Controller
public class FaItemsFormulaController {
	@Autowired
	private FaItemsFormulaService faItemsFormulaService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/credit/saveFaItemsFormula.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		faItemsFormulaService.saveFaItemsFormula(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/credit/updateFaItemsFormula.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		faItemsFormulaService.updateFaItemsFormula(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/credit/deleteFaItemsFormula.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		faItemsFormulaService.deleteFaItemsFormula(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

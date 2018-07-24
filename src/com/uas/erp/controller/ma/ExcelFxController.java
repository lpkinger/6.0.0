package com.uas.erp.controller.ma;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.ma.ExcelFxService;

@Controller
public class ExcelFxController {

	@Autowired
	private ExcelFxService excelFxService;

	/**
	 * 保存
	 */
	@RequestMapping("/ma/saveExcelFx.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelFxService.save(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/ma/deleteExcelFx.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelFxService.delete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 * */
	@RequestMapping("/ma/updateExcelFx.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		excelFxService.update(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

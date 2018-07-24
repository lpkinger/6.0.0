package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleForecastService;

@Controller
public class SaleForecastDrpController extends BaseController {
	@Autowired
	private SaleForecastService saleForecastService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/drp/distribution/saveSaleForecast.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.saveSaleForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/drp/distribution/deleteSaleForecast.action")
	@ResponseBody
	public Map<String, Object> deleteSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.deleteSaleForecast(id, caller);
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
	@RequestMapping("/drp/distribution/updateSaleForecast.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.updateSaleForecastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/drp/distribution/printSaleForecast.action")
	@ResponseBody
	public Map<String, Object> printSaleForecast(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleForecastService.printSaleForecast(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitSaleForecast.action")
	@ResponseBody
	public Map<String, Object> submitSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.submitSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distributione/resSubmitSaleForecast.action")
	@ResponseBody
	public Map<String, Object> resSubmitSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.resSubmitSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditSaleForecast.action")
	@ResponseBody
	public Map<String, Object> auditSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.auditSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("//drp/distribution/resAuditSaleForecast.action")
	@ResponseBody
	public Map<String, Object> resAuditSaleForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.resAuditSaleForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/drp/distribution/SaleForecastChangedate.action")
	@ResponseBody
	public Map<String, Object> SaleForecastChange(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastService.saveSaleForecastChangedate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}
}

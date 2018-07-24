package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.drp.SaleForecastAskService;

@Controller
public class SaleForecastAskController extends BaseController {
	@Autowired
	private SaleForecastAskService saleForecastAskService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/drp/distribution/saveSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.saveSaleForecastAsk(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/drp/distribution/deleteSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> deleteSaleForecast(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.deleteSaleForecastAsk(id, caller);
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
	@RequestMapping("/drp/distribution/updateSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.updateSaleForecastAskById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/drp/distribution/printSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> printSaleForecast(int id, String reportName,
			String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleForecastAskService.printSaleForecastAsk(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> submitSaleForecast(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.submitSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distributione/resSubmitSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> resSubmitSaleForecast(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.resSubmitSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> auditSaleForecast(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.auditSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("//drp/distribution/resAuditSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> resAuditSaleForecast(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.resAuditSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/drp/distribution/SaleForecastAskChangedate.action")
	@ResponseBody
	public Map<String, Object> SaleForecastChange(String caller, String data) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.saveSaleForecastAskChangedate(caller, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/drp/distribution/endSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> endSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.endSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/drp/distribution/resEndSaleForecastAsk.action")
	@ResponseBody
	public Map<String, Object> resEndSale(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleForecastAskService.resEndSaleForecastAsk(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

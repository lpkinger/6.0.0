package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PreForecastClashService;

@Controller
public class PreForecastClashController extends BaseController {
	@Autowired
	private PreForecastClashService preForecastClashService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/savePreForecastClash.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.savePreForecastClash(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/sale/deletePreForecastClash.action")
	@ResponseBody
	public Map<String, Object> deletePreForecastClash(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.deletePreForecastClash(caller, id);
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
	@RequestMapping("/scm/sale/updatePreForecastClash.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.updatePreForecastClashById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitPreForecastClash.action")
	@ResponseBody
	public Map<String, Object> submitPreForecastClash(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.submitPreForecastClash(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitPreForecastClash.action")
	@ResponseBody
	public Map<String, Object> resSubmitPreForecastClash(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.resSubmitPreForecastClash(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditPreForecastClash.action")
	@ResponseBody
	public Map<String, Object> auditPreForecastClash(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preForecastClashService.auditPreForecastClash(id,caller); 
		modelMap.put("success", true);
		return modelMap;
	}
}

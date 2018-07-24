package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.MakeForecastService;

@Controller
public class MakeForecastController extends BaseController {
	@Autowired
	private MakeForecastService makeForecastService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.saveMakeForecast(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> deleteMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.deleteMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.updateMakeForecastById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> printMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.printMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> submitMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.submitMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.resSubmitMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> auditMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.auditMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditMakeForecast.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMakeForecast(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeForecastService.resAuditMakeForecast(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

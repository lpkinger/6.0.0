package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.LabelPrintSettingService;

@Controller
public class LabelPrintSettingController {

	@Autowired
	private LabelPrintSettingService labelPrintSettingService;
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveLPSetting.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.saveLPSetting(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteLPSetting.action")
	@ResponseBody
	public Map<String, Object> deleteLPSetting(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	labelPrintSettingService.deleteLPSetting(id, caller);
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
	@RequestMapping("/pm/mes/updateLPSetting.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.updateLPSetting(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitLPSetting.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.submitLPSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitLPSetting.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.resSubmitLPSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditLPSetting.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.auditLPSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditLPSetting.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		labelPrintSettingService.resAuditLPSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 根据模板编号获取打印caller 和报表名称
	 */
	@RequestMapping("/pm/mes/getPrintCaller.action")
	@ResponseBody
	public Map<String, Object> getPrintCaller(String caller,String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", labelPrintSettingService.getPrintCaller(code, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}

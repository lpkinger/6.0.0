package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.SourceService;

@Controller
public class SourceController {

	@Autowired
	private SourceService sourceService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveSource.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.saveSource(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteSource.action")
	@ResponseBody
	public Map<String, Object> deleteSourceio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	sourceService.deleteSource(id, caller);
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
	@RequestMapping("/pm/mes/updateSource.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.updateSourceById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitSource.action")
	@ResponseBody
	public Map<String, Object> submitSourceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.submitSource(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitSource.action")
	@ResponseBody
	public Map<String, Object> resSubmitSourceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.resSubmitSource(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditSource.action")
	@ResponseBody
	public Map<String, Object> auditSourceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.auditSource(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditSource.action")
	@ResponseBody
	public Map<String, Object> resAuditSourceio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		sourceService.resAuditSource(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

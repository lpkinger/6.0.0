package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.FactoryService;

@Controller
public class FactoryController {

	@Autowired
	private FactoryService factoryService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveFactory.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.saveFactory(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteFactory.action")
	@ResponseBody
	public Map<String, Object> deleteFactoryio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.deleteFactory(id, caller);
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
	@RequestMapping("/pm/mes/updateFactory.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.updateFactoryById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitFactory.action")
	@ResponseBody
	public Map<String, Object> submitFactoryio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.submitFactory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitFactory.action")
	@ResponseBody
	public Map<String, Object> resSubmitFactoryio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.resSubmitFactory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditFactory.action")
	@ResponseBody
	public Map<String, Object> auditFactoryio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.auditFactory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditFactory.action")
	@ResponseBody
	public Map<String, Object> resAuditFactoryio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		factoryService.resAuditFactory(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

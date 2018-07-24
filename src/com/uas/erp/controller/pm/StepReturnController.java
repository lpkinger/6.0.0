package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.StepReturnService;

@Controller
public class StepReturnController {

	@Autowired
	private StepReturnService stepReturnService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveStepReturn.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.saveStepReturn(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteStepReturn.action")
	@ResponseBody
	public Map<String, Object> deleteStepReturnio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	stepReturnService.deleteStepReturn(id, caller);
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
	@RequestMapping("/pm/mes/updateStepReturn.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.updateStepReturnById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitStepReturn.action")
	@ResponseBody
	public Map<String, Object> submitStepReturnio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.submitStepReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitStepReturn.action")
	@ResponseBody
	public Map<String, Object> resSubmitStepReturnio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.resSubmitStepReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditStepReturn.action")
	@ResponseBody
	public Map<String, Object> auditStepReturnio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.auditStepReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditStepReturn.action")
	@ResponseBody
	public Map<String, Object> resAuditStepReturnio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		stepReturnService.resAuditStepReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.WorkShiftService;

@Controller
public class WorkShiftController {

	@Autowired
	private WorkShiftService workShiftService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveWorkShift.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.saveWorkShift(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteWorkShift.action")
	@ResponseBody
	public Map<String, Object> deleteWorkShiftio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	workShiftService.deleteWorkShift(id, caller);
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
	@RequestMapping("/pm/mes/updateWorkShift.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.updateWorkShiftById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitWorkShift.action")
	@ResponseBody
	public Map<String, Object> submitWorkShiftio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.submitWorkShift(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitWorkShift.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkShiftio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.resSubmitWorkShift(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditWorkShift.action")
	@ResponseBody
	public Map<String, Object> auditWorkShiftio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.auditWorkShift(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditWorkShift.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkShiftio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workShiftService.resAuditWorkShift(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

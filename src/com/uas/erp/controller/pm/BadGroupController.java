package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.BadGroupService;

@Controller
public class BadGroupController {

	@Autowired
	private BadGroupService badGroupService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveBadGroup.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.saveBadGroup(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除数据 包括明细
	 */
	@RequestMapping("/pm/mes/deleteBadGroup.action")
	@ResponseBody
	public Map<String, Object> deleteBadGroupio(String caller, int id) {
    	Map<String, Object> modelMap = new HashMap<String, Object>();
    	badGroupService.deleteBadGroup(id, caller);
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
	@RequestMapping("/pm/mes/updateBadGroup.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.updateBadGroupById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitBadGroup.action")
	@ResponseBody
	public Map<String, Object> submitBadGroupio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.submitBadGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitBadGroup.action")
	@ResponseBody
	public Map<String, Object> resSubmitBadGroupio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.resSubmitBadGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditBadGroup.action")
	@ResponseBody
	public Map<String, Object> auditBadGroupio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.auditBadGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditBadGroup.action")
	@ResponseBody
	public Map<String, Object> resAuditBadGroupio(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badGroupService.resAuditBadGroup(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

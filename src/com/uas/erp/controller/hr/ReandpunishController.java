package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.ReandpunishService;

@Controller
public class ReandpunishController {
	@Autowired
	private ReandpunishService reandpunishService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveReandpunish.action")
	@ResponseBody
	public Map<String, Object> saveReandpunish(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.saveReandpunish(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateReandpunish.action")
	@ResponseBody
	public Map<String, Object> updateReandpunish(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.updateReandpunishById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteReandpunish.action")
	@ResponseBody
	public Map<String, Object> deleteReandpunish(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.deleteReandpunish(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitReandpunish.action")
	@ResponseBody
	public Map<String, Object> submitReandpunish(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.submitReandpunish(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitReandpunish.action")
	@ResponseBody
	public Map<String, Object> resSubmitReandpunish(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.resSubmitReandpunish(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditReandpunish.action")
	@ResponseBody
	public Map<String, Object> auditReandpunish(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.auditReandpunish(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditReandpunish.action")
	@ResponseBody
	public Map<String, Object> resAuditReandpunish(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reandpunishService.resAuditReandpunish(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

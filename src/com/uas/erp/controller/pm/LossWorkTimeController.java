package com.uas.erp.controller.pm;

import com.uas.erp.service.pm.LossWorkTimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LossWorkTimeController {

	@Autowired
	private LossWorkTimeService lossWorkTimeService;

	/**
	 * 保存
	 */
	@RequestMapping("/pm/make/saveLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.saveLossWorkTime(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/pm/make/updateLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.updateLossWorkTimeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/make/deleteLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.deleteLossWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/make/submitLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> submitLossWorkTime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.submitLossWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/make/resSubmitLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> resSubmitLossWorkTime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.resSubmitLossWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/make/auditLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> auditLossWorkTime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.auditLossWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/make/resAuditLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> resAuditLossWorkTime(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lossWorkTimeService.resAuditLossWorkTime(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/copyLossWorkTime.action")
	@ResponseBody
	public Map<String, Object> copy(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", lossWorkTimeService.copyLossWorkTime(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.crm.BusinessChanceStageService;

@Controller
public class BusinessChanceStageController {
	@Autowired
	private BusinessChanceStageService BusinessChanceStageService;

	/**
	 * 保存
	 */
	@RequestMapping("/crm/chance/saveBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.saveBusinessChanceStage(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/chance/updateBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.updateBusinessChanceStage(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/chance/deleteBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> deleteBusinessChanceStage(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.deleteBusinessChanceStage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/chance/submitBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> submitBusinessChanceStage(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.submitBusinessChanceStage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/chance/resSubmitBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> resSubmitBusinessChanceStage(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.resSubmitBusinessChanceStage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/chance/auditBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> auditBusinessChanceStage(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.auditBusinessChanceStage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/chance/resAuditBusinessChanceStage.action")
	@ResponseBody
	public Map<String, Object> resAuditBusinessChanceStage(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BusinessChanceStageService.resAuditBusinessChanceStage(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

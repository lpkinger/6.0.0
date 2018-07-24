package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ProjBudgetChangeService;

@Controller
public class ProjBudgetChangeController {
	@Autowired
	private ProjBudgetChangeService projBudgetChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.saveProjBudgetChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> deleteProjBudgetChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.deleteProjBudgetChange(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.updateProjBudgetChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> submitProjBudgetChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.submitProjBudgetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitProjBudgetChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.resSubmitProjBudgetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> auditProjBudgetChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.auditProjBudgetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditProjBudgetChange.action")
	@ResponseBody
	public Map<String, Object> resAuditProjBudgetChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projBudgetChangeService.resAuditProjBudgetChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ChangeBudgetService;

@Controller
public class ChangeBudgetController {
	@Autowired
	private ChangeBudgetService changeBudgetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveChangeBudget.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.saveChangeBudget(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/crm/marketmgr/deleteChangeBudget.action")
	@ResponseBody
	public Map<String, Object> deleteChangeBudget(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.deleteChangeBudget(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateChangeBudget.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.updateChangeBudgetById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitChangeBudget.action")
	@ResponseBody
	public Map<String, Object> submitChangeBudget(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.submitChangeBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitChangeBudget.action")
	@ResponseBody
	public Map<String, Object> resSubmitChangeBudget(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.resSubmitChangeBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditChangeBudget.action")
	@ResponseBody
	public Map<String, Object> auditChangeBudget(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.auditChangeBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditChangeBudget.action")
	@ResponseBody
	public Map<String, Object> resAuditChangeBudget(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeBudgetService.resAuditChangeBudget(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.MProjectPlanService;

@Controller
public class MProjectPlanController {
	@Autowired
	private MProjectPlanService mProjectPlanService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.saveMProjectPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/marketmgr/deleteMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> deleteMProjectPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.deleteMProjectPlan(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.updateMProjectPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> submitMProjectPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.submitMProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitMProjectPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.resSubmitMProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> auditMProjectPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.auditMProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditMProjectPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditMProjectPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.resAuditMProjectPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按照任务模板初步生成任务
	 */
	@RequestMapping("/crm/marketmgr/turnTask.action")
	@ResponseBody
	public Map<String, Object> turnTask(int id, String caller) {
		// id=1;
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mProjectPlanService.turnTask(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 按照任务模板初步生成任务
	 */
	@RequestMapping("/crm/marketmgr/updateTask.action")
	@ResponseBody
	public Map<String, Object> updateTask(String gridStore, String caller) {
		//
		// = ()session.getAttribute("");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// mProjectPlanService.turnTask(id, );
		modelMap.put("success", true);
		return modelMap;
	}
}

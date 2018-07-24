package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.DemandplanService;

@Controller
public class DemandplanController {

	@Autowired
	private DemandplanService demandplanService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/program/saveDemandplan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.saveDemandplan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/program/updateDemandplan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.updateDemandplanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/program/deleteDemandplan.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.deleteDemandplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/program/submitDemandplan.action")
	@ResponseBody
	public Map<String, Object> submitDemandplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.submitDemandplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/program/resSubmitDemandplan.action")
	@ResponseBody
	public Map<String, Object> resSubmitDemandplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.resSubmitDemandplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/program/auditDemandplan.action")
	@ResponseBody
	public Map<String, Object> auditDemandplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.auditDemandplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/program/resAuditDemandplan.action")
	@ResponseBody
	public Map<String, Object> resAuditDemandplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.resAuditDemandplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转用人申请单
	 */
	@RequestMapping("/hr/emplmana/demandTurn.action")
	@ResponseBody
	public Map<String, Object> demandTurn(String caller, int id, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		demandplanService.demandTurn(caller, id, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 年度用人计划批量转用人申请单
	 */
	@RequestMapping(value = "/hr/vastTurnRecruitment.action")
	@ResponseBody
	public Map<String, Object> vastTurnRecruitment(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", demandplanService.vastTurnRecruitment(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

}

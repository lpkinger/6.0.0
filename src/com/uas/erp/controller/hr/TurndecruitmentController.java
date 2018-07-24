package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TurndecruitmentService;

@Controller
public class TurndecruitmentController {

	@Autowired
	private TurndecruitmentService turndecruitmentService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.saveTurndecruitment(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.updateTurndecruitmentById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.deleteTurndecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> submitTurndecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.submitTurndecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurndecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.resSubmitTurndecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> auditTurndecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.auditTurndecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTurndecruitment.action")
	@ResponseBody
	public Map<String, Object> resAuditTurndecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turndecruitmentService.resAuditTurndecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

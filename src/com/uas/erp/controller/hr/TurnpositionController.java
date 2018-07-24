package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.hr.TurnpositionService;

@Controller
public class TurnpositionController {

	@Autowired
	private TurnpositionService turnpositionService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveTurnposition.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.saveTurnposition(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTurnposition.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.updateTurnpositionById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTurnposition.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.deleteTurnposition(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTurnposition.action")
	@ResponseBody
	public Map<String, Object> submitTurnposition(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.submitTurnposition(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTurnposition.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnposition(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.resSubmitTurnposition(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTurnposition.action")
	@ResponseBody
	public Map<String, Object> auditTurnposition(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.auditTurnposition(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTurnposition.action")
	@ResponseBody
	public Map<String, Object> resAuditTurnposition(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnpositionService.resAuditTurnposition(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
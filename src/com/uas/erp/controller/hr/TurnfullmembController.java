package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TurnfullmembService;

@Controller
public class TurnfullmembController {

	@Autowired
	private TurnfullmembService turnfullmembService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.saveTurnfullmemb(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.updateTurnfullmembById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.deleteTurnfullmemb(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> submitTurnfullmemb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.submitTurnfullmemb(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnfullmemb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.resSubmitTurnfullmemb(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> auditTurnfullmemb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.auditTurnfullmemb(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTurnfullmemb.action")
	@ResponseBody
	public Map<String, Object> resAuditTurnfullmemb(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnfullmembService.resAuditTurnfullmemb(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 
	 */
	@RequestMapping("/hr/emplmana/vastZhuanz.action")
	@ResponseBody
	public Map<String, Object> vastZhuanz(String caller, String data) {
		turnfullmembService.vastZhuanz(data, caller);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}
}

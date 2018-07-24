package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TurnoverService;

@Controller
public class TurnoverController {

	@Autowired
	private TurnoverService turnoverService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveTurnover.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.saveTurnover(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTurnover.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.updateTurnoverById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTurnover.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.deleteTurnover(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTurnover.action")
	@ResponseBody
	public Map<String, Object> submitTurnover(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.submitTurnover(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTurnover.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnover(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.resSubmitTurnover(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTurnover.action")
	@ResponseBody
	public Map<String, Object> auditTurnover(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.auditTurnover(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTurnover.action")
	@ResponseBody
	public Map<String, Object> resAuditTurnover(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		turnoverService.resAuditTurnover(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
     * 确认离职
	 */
	@RequestMapping("hr/emplmana/confirmTurnover.action")
	@ResponseBody
	public Map<String, Object> confirmTurnover(String caller, String data){
		Map<String, Object> modelMap=new HashMap<String, Object>();
		String log=turnoverService.confirmTurnover(caller,data);
		modelMap.put("log",log);
		modelMap.put("success", true);
		return modelMap;
	}
}

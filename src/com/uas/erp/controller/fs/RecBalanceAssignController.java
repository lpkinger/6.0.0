package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.RecBalanceAssignService;

@Controller
public class RecBalanceAssignController {

	@Autowired
	private RecBalanceAssignService recBalanceAssignService;

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.updateRecBalanceAssign(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.deleteRecBalanceAssign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> submitRecBalanceAssign(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.submitRecBalanceAssign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecBalanceAssign(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.resSubmitRecBalanceAssign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> auditRecBalanceAssign(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.auditRecBalanceAssign(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收账款转让
	 */
	@RequestMapping("/fs/cust/assignRecBalance.action")
	@ResponseBody
	public Map<String, Object> assign(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recBalanceAssignService.assignRecBalance(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成应收账款转让单
	 */
	@RequestMapping(value = "/fs/cust/turnRecBalanceAssign.action")
	@ResponseBody
	public Map<String, Object> vastMakeCraftTurnAccept(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = recBalanceAssignService.turnRecBalanceAssign(data, caller);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

}

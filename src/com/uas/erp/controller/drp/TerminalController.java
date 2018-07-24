package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.TerminalService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TerminalController {

	@Autowired
	private TerminalService terminalService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/saveTerminal.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.saveTerminal(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updateTerminal.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.updateTerminalById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deleteTerminal.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.deleteTerminal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitTerminal.action")
	@ResponseBody
	public Map<String, Object> submitTerminal(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.submitTerminal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitTerminal.action")
	@ResponseBody
	public Map<String, Object> resSubmitTerminal(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.resSubmitTerminal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditTerminal.action")
	@ResponseBody
	public Map<String, Object> auditTerminal(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.auditTerminal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditTerminal.action")
	@ResponseBody
	public Map<String, Object> resAuditTerminal(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		terminalService.resAuditTerminal(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

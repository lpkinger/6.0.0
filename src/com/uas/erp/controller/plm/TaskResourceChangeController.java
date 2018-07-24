package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.TaskResourceChangeService;

@Controller
public class TaskResourceChangeController {
	@Autowired
	private TaskResourceChangeService taskResourceChangeService;

	@RequestMapping("/plm/change/saveTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> saveTaskResourceChange(HttpSession session, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.saveTaskResourceChange(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/deleteTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> deleteTaskResourceChange(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.deleteTaskResourceChange(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/updateTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> updateTaskResourceChange(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.updateTaskResourceChange(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/change/submitTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> submitTaskResourceChange(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.submitTaskResourceChange(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/change/resSubmitTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitTaskResourceChange(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.resSubmitTaskResourceChange(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/change/auditTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> auditTaskResourceChange(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.auditTaskResourceChange(id,"TaskResourceChange");
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/change/resAuditTaskResourceChange.action")
	@ResponseBody
	public Map<String, Object> resAuditTaskResourceChange(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.resAuditTaskResourceChange(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 批量变更
	 */
	@RequestMapping("/plm/change/batchResourceChange.action")
	@ResponseBody
	public Map<String, Object> batchResourceChange(HttpServletRequest req, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskResourceChangeService.batchRescourceChange(data);
		modelMap.put("success", true);
		return modelMap;
	}
}

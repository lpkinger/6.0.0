package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.WorkWeeklyService;

@Controller
public class WorkWeeklyController {
	@Autowired
	private WorkWeeklyService workWeeklyService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/persontask/saveWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.saveWorkWeekly(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/updateWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.updateWorkWeekly(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/deleteWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.deleteWorkWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 提交
	 */
	@RequestMapping("/oa/persontask/submitWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> submitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.submitWorkWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/persontask/resSubmitWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.resSubmitWorkWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/oa/persontask/auditWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> auditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.auditWorkWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/oa/persontask/resAuditWorkWeekly.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.resAuditWorkWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 抓取工作内容
	 */
	@RequestMapping("/oa/persontask/catchWorkContentWeekly.action")
	@ResponseBody
	public Map<String, Object> catchWorkContent(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workWeeklyService.catchWorkContentWeekly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

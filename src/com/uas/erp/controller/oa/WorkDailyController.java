package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.WorkDailyService;

@Controller
public class WorkDailyController {
	@Autowired
	private WorkDailyService workDailyService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/persontask/saveWorkDaily.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.saveWorkDaily(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/updateWorkDaily.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.updateWorkDaily(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/deleteWorkDaily.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.deleteWorkDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 提交
	 */
	@RequestMapping("/oa/persontask/submitWorkDaily.action")
	@ResponseBody
	public Map<String, Object> submitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.submitWorkDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/persontask/resSubmitWorkDaily.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.resSubmitWorkDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/oa/persontask/auditWorkDaily.action")
	@ResponseBody
	public Map<String, Object> auditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.auditWorkDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/oa/persontask/resAuditWorkDaily.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.resAuditWorkDaily(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 抓取工作内容
	 */
	@RequestMapping("/oa/persontask/catchWorkContent.action")
	@ResponseBody
	public Map<String, Object> catchWorkContent(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workDailyService.catchWorkContent(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

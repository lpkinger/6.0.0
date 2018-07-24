package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.WorkMonthlyService;

@Controller
public class WorkMonthlyController {
	@Autowired
	private WorkMonthlyService workMonthlyService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/persontask/saveWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.saveWorkMonthly(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/updateWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.updateWorkMonthly(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/deleteWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.deleteWorkMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 提交
	 */
	@RequestMapping("/oa/persontask/submitWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> submitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.submitWorkMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/persontask/resSubmitWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> resSubmitWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.resSubmitWorkMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/oa/persontask/auditWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> auditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.auditWorkMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/oa/persontask/resAuditWorkMonthly.action")
	@ResponseBody
	public Map<String, Object> resAuditWorkReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.resAuditWorkMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 抓取工作内容
	 */
	@RequestMapping("/oa/persontask/catchWorkContentMonthly.action")
	@ResponseBody
	public Map<String, Object> catchWorkContent(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workMonthlyService.catchWorkContentMonthly(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

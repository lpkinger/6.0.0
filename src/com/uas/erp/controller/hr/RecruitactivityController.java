package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RecruitactivityService;

@Controller
public class RecruitactivityController {

	@Autowired
	private RecruitactivityService recruitactivityService;

	/**
	 * 保存recruitactivity
	 */
	@RequestMapping("/hr/emplmana/saveRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.saveRecruitactivity(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.updateRecruitactivityById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.deleteRecruitactivity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> submitRecruitactivity(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.submitRecruitactivity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecruitactivity(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.resSubmitRecruitactivity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> auditRecruitactivity(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.auditRecruitactivity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRecruitactivity.action")
	@ResponseBody
	public Map<String, Object> resAuditRecruitactivity(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitactivityService.resAuditRecruitactivity(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

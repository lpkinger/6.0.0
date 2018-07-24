package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RecruitplanService;

@Controller
public class RecruitplanController {

	@Autowired
	private RecruitplanService recruitplanService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveRecruitplan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.saveRecruitplan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateRecruitplan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.updateRecruitplanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteRecruitplan.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.deleteRecruitplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitRecruitplan.action")
	@ResponseBody
	public Map<String, Object> submitRecruitplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.submitRecruitplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitRecruitplan.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecruitplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.resSubmitRecruitplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRecruitplan.action")
	@ResponseBody
	public Map<String, Object> auditRecruitplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.auditRecruitplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRecruitplan.action")
	@ResponseBody
	public Map<String, Object> resAuditRecruitplan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitplanService.resAuditRecruitplan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

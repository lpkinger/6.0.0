package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RecruitmentService;

@Controller
public class RecruitmentController {

	@Autowired
	private RecruitmentService recruitmentService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveRecruitment.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.saveRecruitment(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateRecruitment.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.updateRecruitmentById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteRecruitment.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.deleteRecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitRecruitment.action")
	@ResponseBody
	public Map<String, Object> submitRecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.submitRecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitRecruitment.action")
	@ResponseBody
	public Map<String, Object> resSubmitRecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.resSubmitRecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRecruitment.action")
	@ResponseBody
	public Map<String, Object> auditRecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.auditRecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRecruitment.action")
	@ResponseBody
	public Map<String, Object> resAuditRecruitment(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitmentService.resAuditRecruitment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/*
	 * 转招聘计划
	 */
	@RequestMapping("/hr/emplmana/turnRecruitplan.action")
	@ResponseBody
	public Map<String, Object> turnToRecruitplan(String caller,
			String formdata, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", recruitmentService.turnRecruitplan(formdata, param, caller));
		modelMap.put("success", true);
		return modelMap;

	}
}

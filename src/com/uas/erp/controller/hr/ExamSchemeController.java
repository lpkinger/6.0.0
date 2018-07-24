package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.ExamSchemeService;

@Controller
public class ExamSchemeController {
	@Autowired
	private ExamSchemeService examSchemeService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/emplmana/saveExamScheme.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.saveExamScheme(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteExamScheme.action")
	@ResponseBody
	public Map<String, Object> deleteExamScheme(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.deleteExamScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/emplmana/updateExamScheme.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.updateExamSchemeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitExamScheme.action")
	@ResponseBody
	public Map<String, Object> submitExamScheme(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.submitExamScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitExamScheme.action")
	@ResponseBody
	public Map<String, Object> resSubmitExamScheme(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.resSubmitExamScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditExamScheme.action")
	@ResponseBody
	public Map<String, Object> auditExamScheme(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.auditExamScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditExamScheme.action")
	@ResponseBody
	public Map<String, Object> resAuditExamScheme(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		examSchemeService.resAuditExamScheme(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.QuaProjectService;

@Controller
public class QuaProjectController {
	@Autowired
	QuaProjectService quaProjectService;

	@RequestMapping("/scm/qc/saveQuaProject.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.saveQuaProject(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/qc/deleteQuaProject.action")
	@ResponseBody
	public Map<String, Object> deleteQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.deleteQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/qc/updateQuaProject.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.updateQuaProjectById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/qc/printQuaProject.action")
	@ResponseBody
	public Map<String, Object> printQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.printQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/qc/auditQuaProject.action")
	@ResponseBody
	public Map<String, Object> auditQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.auditQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/qc/resAuditQuaProject.action")
	@ResponseBody
	public Map<String, Object> resAuditQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.resAuditQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/submitQuaProject.action")
	@ResponseBody
	public Map<String, Object> submitQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.submitQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/qc/resSubmitQuaProject.action")
	@ResponseBody
	public Map<String, Object> resSubmitQuaProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quaProjectService.resSubmitQuaProject(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}

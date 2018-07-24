package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ResearchProjectService;

@Controller
public class ResearchProjectController {
	@Autowired
	private ResearchProjectService researchProjectService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveResearchProject.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.saveResearchProject(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteResearchProject.action")
	@ResponseBody
	public Map<String, Object> deleteResearchProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.deleteResearchProject(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateResearchProject.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.updateResearchProject(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitResearchProject.action")
	@ResponseBody
	public Map<String, Object> submitResearchProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.submitResearchProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitResearchProject.action")
	@ResponseBody
	public Map<String, Object> resSubmitResearchProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.resSubmitResearchProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditResearchProject.action")
	@ResponseBody
	public Map<String, Object> auditResearchProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.auditResearchProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditResearchProject.action")
	@ResponseBody
	public Map<String, Object> resAuditResearchProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchProjectService.resAuditResearchProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ChangeProjectService;

@Controller
public class ChangeProjectController {
	@Autowired
	private ChangeProjectService changeProjectService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/marketmgr/saveChangeProject.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.saveChangeProject(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除ECN数据 包括ECN明细
	 */
	@RequestMapping("/crm/marketmgr/deleteChangeProject.action")
	@ResponseBody
	public Map<String, Object> deleteChangeProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.deleteChangeProject(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateChangeProject.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.updateChangeProject(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitChangeProject.action")
	@ResponseBody
	public Map<String, Object> submitChangeProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.submitChangeProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitChangeProject.action")
	@ResponseBody
	public Map<String, Object> resSubmitChangeProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.resSubmitChangeProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditChangeProject.action")
	@ResponseBody
	public Map<String, Object> auditChangeProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.auditChangeProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditChangeProject.action")
	@ResponseBody
	public Map<String, Object> resAuditChangeProject(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeProjectService.resAuditChangeProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

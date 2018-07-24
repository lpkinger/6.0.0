package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectTGService;

@Controller
public class ProjectTGController {
	@Autowired
	private ProjectTGService projectTGService;

	/**
	 * 保存
	 */
	@RequestMapping("plm/project/saveProjectTG.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTGService.saveProjectTG(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制表单 简化录入工作
	 */
	@RequestMapping("plm/project/copyProjectTG.action")
	@ResponseBody
	public Map<String, Object> copy(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = projectTGService.copy(id);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/plm/project/deleteProjectTG.action")
	@ResponseBody
	public Map<String, Object> deletePrjManChange(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTGService.deleteProjectTG(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更新
	 */
	@RequestMapping("plm/project/updateProjectTG.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTGService.updateProjectTG(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("plm/project/auditProjectTG.action")
	@ResponseBody
	public Map<String, Object> audit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTGService.auditProjectTG(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("plm/project/resAuditProjectTG.action")
	@ResponseBody
	public Map<String, Object> resAudit(HttpSession session, int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTGService.resAuditProjectTG(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

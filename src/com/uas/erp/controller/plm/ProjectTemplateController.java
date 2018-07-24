package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectTemplateService;

@Controller
public class ProjectTemplateController {
	@Autowired
	private ProjectTemplateService ProjectTemplateService;

	@RequestMapping("/plm/project/getProjectTemplate.action")
	@ResponseBody
	public Map<String, Object> getData(HttpSession session, String condition, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String data = ProjectTemplateService.getProjectTemplateData(caller, condition);
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/saveProjectTemplate.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProjectTemplateService.saveProjectTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/deleteProjectTemplate.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProjectTemplateService.deleteProjectTemplate(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/updateProjectTemplate.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ProjectTemplateService.updateProjectTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}

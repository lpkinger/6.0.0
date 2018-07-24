package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectColorService;
import com.uas.erp.service.plm.ProjectTeamRoleService;

@Controller
public class ProjectTeamRoleController {
	@Autowired
	private ProjectColorService projectColorService;
	@Autowired
	private ProjectTeamRoleService projectTeamRoleService;
	@RequestMapping("/plm/project/saveProjectTeamRole.action")
	@ResponseBody
	public Map<String, Object> saveProjectTeamRole(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTeamRoleService.saveProjectTeamRole(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/deleteProjectTeamRole.action")
	@ResponseBody
	public Map<String, Object> deleteProjectTeamRole(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTeamRoleService.deleteProjectTeamRole(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/project/updateProjectTeamRole.action")
	@ResponseBody
	public Map<String, Object> updateProjectTeamRole(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		projectTeamRoleService.updateProjectTeamRole(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

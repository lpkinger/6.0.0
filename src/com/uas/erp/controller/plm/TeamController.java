package com.uas.erp.controller.plm;

import java.sql.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Team;
import com.uas.erp.service.plm.TeamService;

@Controller
public class TeamController extends BaseController {
	@Autowired
	private TeamService teamService;

	@RequestMapping("plm/team/saveTeam.action")
	@ResponseBody
	public Map<String, Object> saveTeam(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.saveTeam(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/team/deleteTeam.action")
	@ResponseBody
	public Map<String, Object> deleteTeam(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.deleteTeam(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细行某一条数据
	 */
	@RequestMapping("/plm/team/deleteDetail.action")
	@ResponseBody
	public Map<String, Object> deleteDetail(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.deleteDetail(id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/team/updateTeam.action")
	@ResponseBody
	public Map<String, Object> updateTeam(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.updateTeamById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/plm/team/getTeam.action")
	@ResponseBody
	public Map<String, Object> getTeam(HttpSession session, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Team team = teamService.getTeamByCode(code);
		modelMap.put("success", true);
		modelMap.put("team", team);
		return modelMap;
	}

	@RequestMapping("/plm/team/insertTeam.action")
	@ResponseBody
	public Map<String, Object> insertTeam(HttpSession session, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.insert(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/team/copyTeam.action")
	@ResponseBody
	public Map<String, Object> copyTeam(int id,String caller, String formStore,
			String param, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		teamService.copyTeam(id,code, formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/plm/team/teamToMeeting.action")
	@ResponseBody
	public Map<String, Object> teamToMeeting(String caller, String id){
		return teamService.teamToMeeting(caller, id);
	}
}

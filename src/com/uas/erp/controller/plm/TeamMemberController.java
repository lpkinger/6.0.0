package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Teammember;
import com.uas.erp.service.plm.TeamMemberService;
@Controller
public class TeamMemberController extends BaseController{
	@Autowired
	private TeamMemberService teammemberService;
	@RequestMapping("plm/teammember/saveTeamMember.action")
	@ResponseBody
	public Map<String ,Object> saveTeamMember(HttpSession session,String formStore,String param){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		teammemberService.saveTeamMember(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/plm/teammember/getTeamMember.action")  
	@ResponseBody 
	public Map<String, Object> getTeam(HttpSession session, String employee_code, int team_id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Teammember teammember = teammemberService.getTeamMemberByIdCode(team_id, employee_code);
		modelMap.put("success", true);
		modelMap.put("teammember", teammember);
		return modelMap;
	}
}

package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.hr.JobPowerService;

@Controller
public class JobPowerController {

	@Autowired
	private JobPowerService jobPowerService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/employee/updateJobPower.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String update, Boolean _self,String utype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobPowerService.update(update, caller, _self);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存角色权限
	 */
	@RequestMapping("/hr/employee/updateRolePower.action")
	@ResponseBody
	public Map<String, Object> updateRolePower(String caller, String update, Boolean _self,String utype) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jobPowerService.updateRolePower(update, caller, _self);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/hr/employee/getJobPower.action")
	@ResponseBody
	public Map<String, Object> get(String caller, int parentid, int joid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// List<Power> powers = powerService.getPowersByParentID(parentid);
		// List<PositionPower> positionPowers = new ArrayList<PositionPower>();
		// modelMap.put("powers", powers);
		// for(Power p:powers){
		// if(p.getPo_isleaf().equals("T")){
		// positionPowers.add(jobPowerService.getPPByPoIdAndJoID(p.getPo_id(),
		// joid));
		// }
		// }
		// modelMap.put("positionPowers", positionPowers);
		modelMap.put("success", true);
		return modelMap;
	}
}

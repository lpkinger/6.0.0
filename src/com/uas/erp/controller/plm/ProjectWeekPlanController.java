package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ProjectRequestService;
import com.uas.erp.service.plm.ProjectWeekPlanService;



@Controller
public class ProjectWeekPlanController {
	@Autowired
	private ProjectWeekPlanService projectWeekPlanService;
	
	
	/*
	 * 取项目列表(结合产品类型)
	 */
	@RequestMapping(value = "/plm/task/getProjectAndProductList.action")
	@ResponseBody
	public Map<String,Object> getProjectList() {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		modelMap = projectWeekPlanService.getProjectList();
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 更新项目
	 */
	@RequestMapping(value = "/plm/task/updateProject.action")
	@ResponseBody
	public Map<String,Object> getProjectList(String formStore) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		projectWeekPlanService.updateProject(formStore);;
		modelMap.put("success", true);
		return modelMap;
	}
	
}

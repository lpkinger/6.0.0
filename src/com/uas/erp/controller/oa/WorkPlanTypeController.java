package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.WorkPlanTypeService;

@Controller
public class WorkPlanTypeController {
	@Autowired
	private WorkPlanTypeService workPlanTypeService;

	/**
	 * 保存
	 */
	@RequestMapping("/oa/persontask/workPlan/saveWorkPlanType.action")
	@ResponseBody
	public Map<String, Object> save(String caller, HttpServletRequest request,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanTypeService.saveWorkPlanType(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/persontask/workPlan/updateWorkPlanType.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanTypeService.updateWorkPlanType(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/persontask/workPlan/deleteWorkPlanType.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workPlanTypeService.deleteWorkPlanType(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

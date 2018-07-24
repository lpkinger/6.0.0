package com.uas.erp.controller.plm;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.TaskPlanService;

@Controller
public class TaskPlanController {

	@Autowired
	private TaskPlanService taskPlanService;

	/**
	 * 保存TaskPlan
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/plm/task/saveTaskPlan.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.saveTaskPlan(formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/plm/task/updateTaskPlan.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.updateTaskPlanById(formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/plm/task/deleteTaskPlan.action")
	@ResponseBody
	public Map<String, Object> delete(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.deleteTaskPlan(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/plm/task/submitTaskPlan.action")
	@ResponseBody
	public Map<String, Object> submitTaskPlan(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.submitTaskPlan(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/plm/task/resSubmitTaskPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitTaskPlan(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.resSubmitTaskPlan(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/plm/task/auditTaskPlan.action")
	@ResponseBody
	public Map<String, Object> auditTaskPlan(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.auditTaskPlan(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/plm/task/resAuditTaskPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditTaskPlan(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskPlanService.resAuditTaskPlan(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取当前时间的周数
	 */
	@RequestMapping("/plm/task/getWeek.action")
	@ResponseBody
	public Map<String, Object> getWeek(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Object[] objs=taskPlanService.getWeek();
		modelMap.put("week", objs[0]);
		modelMap.put("year", objs[1]);
		return modelMap;
	}
	
	
	
}

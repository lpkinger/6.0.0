package com.uas.erp.controller.hr;

import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TrainingPlanService;

@Controller
public class TrainingPlanController {

	@Autowired
	private TrainingPlanService trainingPlanService;

	/**
	 * 保存
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/saveTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.saveTrainingPlan(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/updateTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.updateTrainingPlanById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.deleteTrainingPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/submitTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> submitTrain(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.submitTrainingPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.resSubmitTrainingPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * @throws ParseException 
	 */
	@RequestMapping("/hr/emplmana/auditTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> auditTrain(String caller, int id) throws ParseException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.auditTrainingPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTrainingPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingPlanService.resAuditTrainingPlan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 课程培训模板
	 */
	@RequestMapping("/hr/emplmana/getTrainingCourse.action")
	@ResponseBody
	public Map<String, Object> getTrainingCourse(String caller, String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", trainingPlanService.getTrainingCourse(code));		;
		modelMap.put("success", true);
		return modelMap;
	}
}

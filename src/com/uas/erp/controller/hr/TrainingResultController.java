package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TrainingResultService;

@Controller
public class TrainingResultController {

	@Autowired
	private TrainingResultService trainingResultService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/emplmana/saveTrainingResult.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.saveTrainingResult(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTrainingResult.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.updateTrainingResultById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTrainingResult.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.deleteTrainingResult(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTrainingResult.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.submitTrainingResult(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTrainingResult.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.resSubmitTrainingResult(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTrainingResult.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainingResultService.auditTrainingResult(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.TrainService;

@Controller
public class TrainController {

	@Autowired
	private TrainService trainService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveTrain.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.saveTrain(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateTrain.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.updateTrainById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteTrain.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.deleteTrain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitTrain.action")
	@ResponseBody
	public Map<String, Object> submitTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.submitTrain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitTrain.action")
	@ResponseBody
	public Map<String, Object> resSubmitTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.resSubmitTrain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditTrain.action")
	@ResponseBody
	public Map<String, Object> auditTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.auditTrain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditTrain.action")
	@ResponseBody
	public Map<String, Object> resAuditTrain(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainService.resAuditTrain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

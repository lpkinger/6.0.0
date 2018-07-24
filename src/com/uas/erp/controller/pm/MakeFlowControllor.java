package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.pm.MakeFlowService;

@Controller
public class MakeFlowControllor {

	@Autowired
	private MakeFlowService makeFlowService;

	@RequestMapping("/pm/make/saveMakeFlow.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeFlowService.saveMakeFlow(gridStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除BOM数据 包括BOM明细
	 */
	@RequestMapping("/pm/make/deleteMakeflow.action")
	@ResponseBody
	public Map<String, Object> deleteMakeChange(int id,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeFlowService.deleteMakeFlow(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/pm/make/CheckdeleteMakeflow.action")
	@ResponseBody
	public Map<String, Object> ChekcdeleteMakeChange(String caller,
			String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = makeFlowService.CheckdeleteMakeFlow(code, caller);
		modelMap.put("log", log);
		return modelMap;
	}

	@RequestMapping("/pm/make/printMakeflow.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = makeFlowService.printMakeFlow(id,reportName,
				condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/*
	 * yaozx@14-02-09 自动分拆流程单
	 */
	@RequestMapping("/pm/make/newmakeflows.action")
	@ResponseBody
	public Map<String, Object> makeflows(String caller, int id, int number,
			int mfqty, String date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeFlowService.makeMakeFlows(id, number, mfqty, date, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

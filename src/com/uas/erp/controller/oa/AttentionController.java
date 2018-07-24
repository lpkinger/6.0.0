package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.oa.AttentionService;

@Controller
public class AttentionController {
	@Autowired
	private AttentionService attentionService;

	@RequestMapping("/oa/attention/saveAttentionGrade.action")
	@ResponseBody
	public Map<String, Object> saveAttentionGrade(String caller,
			String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attentionService.saveAttentionGrade(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/attention/deleteAttentionGrade.action")
	@ResponseBody
	public Map<String, Object> deleteAttentionGrade(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attentionService.deleteAttentionGrade(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/attention/saveAttentionSub.action")
	@ResponseBody
	public Map<String, Object> saveAttentionSub(String caller,
			String formStore, String param, String mutiselected) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attentionService.saveAttentionSub(caller, formStore, param,
				mutiselected);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/attention/getAttentionDataAndColumns.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition,
			int page, int pageSize) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel panel = attentionService.getGridPanel(caller);
		modelMap.put("fields", panel.getGridFields());
		modelMap.put("columns", panel.getGridColumns());
		if (condition.equals("")) {
		} else {
			modelMap.put("data",
					attentionService.getData(caller, condition, page, pageSize)
							.toString());
		}
		return modelMap;
	}

	@RequestMapping("/oa/attention/getAaccreditDataAndColumns.action")
	@ResponseBody
	public Map<String, Object> getAaccreditDataAndColumns(String caller,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel panel = attentionService.getGridPanel(caller);
		modelMap.put("fields", panel.getGridFields());
		modelMap.put("columns", panel.getGridColumns());
		if (condition.equals("")) {
		} else {
			modelMap.put("data",
					attentionService.getAaccreditData(caller, condition)
							.toString());
		}
		return modelMap;
	}

	@RequestMapping("/oa/attention/getAttentionByEmId.action")
	@ResponseBody
	public Map<String, Object> getAttentionsByEmId(String caller, int emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", attentionService.getAttentionsByEmId(caller, emid));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/common/getAttentionEmployee.action")
	@ResponseBody
	public Map<String, Object> getAttentionEmployee(String caller, int emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", BaseUtil.parseMap2Str(attentionService
				.getEmployeeData(caller, emid)));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/attention/getAttentionEmployeeByParam.action")
	@ResponseBody
	public Map<String, Object> getAttentionEmployee(String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", BaseUtil.parseMap2Str(attentionService
				.getEmployeeDataByParam(caller, param)));
		modelMap.put("success", true);
		return modelMap;
	}

	// 判断该员工是否在线
	@RequestMapping("oa/attention/CheckISOnline.action")
	@ResponseBody
	public Map<String, Object> checkOnline(String caller, int emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", attentionService.ChekOnlineEmployee(emid));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("oa/attention/deleteAttentions.action")
	@ResponseBody
	public Map<String, Object> deleteAttentions(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attentionService.deleteAttentions(data,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("oa/attention/getEmployees.action")
	@ResponseBody
	public Map<String, Object> getEmployees(String caller, int emid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("tree", attentionService.getEmployees(caller, emid));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("oa/attention/AttentionCounts.action")
	@ResponseBody
	public Map<String, Object> getAttentionCounts(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("count", attentionService.getAttentionCounts(caller));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/attention/getDataAndColumnsByParam.action")
	@ResponseBody
	public Map<String, Object> getDataAndColumns(String caller, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		GridPanel panel = attentionService.getGridPanel(caller);
		modelMap.put("fields", panel.getGridFields());
		modelMap.put("columns", panel.getGridColumns());
		modelMap.put("data",
				attentionService.getAttentionDataByParam(param, caller)
						.toString());
		return modelMap;
	}
}

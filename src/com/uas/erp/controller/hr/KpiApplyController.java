package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.SpObserver;
import com.uas.erp.model.GridPanel;
import com.uas.erp.service.hr.KpiApplyService;
import com.uas.erp.service.ma.ConfigService;

@Controller
public class KpiApplyController {
	@Autowired
	private KpiApplyService kpiApplyService;

	@Autowired
	private ConfigService configService;
	
	/**
	 * 保存
	 */
	@RequestMapping("/hr/kpi/saveKpiApply.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.saveKpiApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/kpi/updateKpiApply.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String param,String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.updateKpiApply(formStore,param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kpi/deleteKpiApply.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.deleteKpiApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/hr/kpi/auditKpiApply.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.auditKpiApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kpi/resAuditKpiApply.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.resAuditKpiApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/hr/kpi/submitKpiApply.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.submitKpiApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kpi/resSubmitKpiApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiApplyService.resSubmitKpiApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping(value = "/hr/kpi/getGridPanel.action")
	@ResponseBody
	public Map<String, Object> getGridFields(String caller, String condition, Integer start,
			Integer end, String master, Integer _m) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (master != null && !master.equals(""))
			SpObserver.putSp(master);
		GridPanel gridPanel = kpiApplyService.getGridPanel(caller, condition, start,end, _m);
		modelMap.put("fields", gridPanel.getGridFields());
		modelMap.put("columns", gridPanel.getGridColumns());
		modelMap.put("dbfinds", gridPanel.getDbfinds());
		modelMap.put("limits", gridPanel.getLimits());
		modelMap.put("data", gridPanel.getDataString());
		// 必填项label特殊颜色
		JSONObject config = configService.getConfigByCallerAndCode("sys", "necessaryFieldColor");
		if (config != null && config.get("data") != null)
			modelMap.put("necessaryFieldColor", config.get("data"));
		return modelMap;
	}
}

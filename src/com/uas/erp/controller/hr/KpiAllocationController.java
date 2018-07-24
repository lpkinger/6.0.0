package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpiAllocationService;

@Controller
public class KpiAllocationController {
	@Autowired
	private KpiAllocationService kpiAllocationService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kpi/saveKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.saveKpiAllocation(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kpi/deleteKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> deleteKpiAllocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.deleteKpiAllocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kpi/updateKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.updateKpiAllocationById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/kpi/submitKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> submitKpiAllocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.submitKpiAllocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kpi/resSubmitKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> resSubmitKpiAllocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.resSubmitKpiAllocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/kpi/auditKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> auditKpiAllocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.auditKpiAllocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/kpi/resAuditKpiAllocation.action")
	@ResponseBody
	public Map<String, Object> resAuditKpiAllocation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpiAllocationService.resAuditKpiAllocation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

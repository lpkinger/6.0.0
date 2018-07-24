package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KpibillChangeService;


@Controller
public class KpibillChangeController {
	@Autowired
	private KpibillChangeService kpibillChangeService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kpi/saveKpibillChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.saveKpibillChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kpi/deleteKpibillChange.action")
	@ResponseBody
	public Map<String, Object> deleteKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.deleteKpibillChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kpi/updateKpibillChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.updateKpibillChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/kpi/submitKpibillChange.action")
	@ResponseBody
	public Map<String, Object> submitKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.submitKpibillChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kpi/resSubmitKpibillChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitKpibillChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.resSubmitKpibillChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/kpi/auditKpibillChange.action")
	@ResponseBody
	public Map<String, Object> auditKpibillChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kpibillChangeService.auditKpibillChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

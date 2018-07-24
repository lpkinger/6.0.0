package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestMapping;
import com.uas.erp.service.hr.CCSQChangeService;

@Controller
public class CCSQChangeController {
	@Autowired
	private CCSQChangeService ccsqChangeService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("hr/attendance/saveCCSQChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.saveCCSQChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/attendance/deleteCCSQChange.action")
	@ResponseBody
	public Map<String, Object> deleteCCSQChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.deleteCCSQChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/attendance/updateCCSQChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.updateCCSQChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/attendance/submitCCSQChange.action")
	@ResponseBody
	public Map<String, Object> submitCCSQChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.submitCCSQChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/attendance/resSubmitCCSQChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitCCSQChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.resSubmitCCSQChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/attendance/auditCCSQChange.action")
	@ResponseBody
	public Map<String, Object> auditCCSQChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.auditCCSQChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/attendance/resAuditCCSQChange.action")
	@ResponseBody
	public Map<String, Object> resAuditCCSQChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ccsqChangeService.resAuditCCSQChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

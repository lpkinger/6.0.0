package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RetireService;

@Controller
public class RetireController {

	@Autowired
	private RetireService retireService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveRetire.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.saveRetire(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateRetire.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.updateRetireById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteRetire.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.deleteRetire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/emplmana/submitRetire.action")
	@ResponseBody
	public Map<String, Object> submitRetire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.submitRetire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/emplmana/resSubmitRetire.action")
	@ResponseBody
	public Map<String, Object> resSubmitRetire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.resSubmitRetire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRetire.action")
	@ResponseBody
	public Map<String, Object> auditRetire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.auditRetire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRetire.action")
	@ResponseBody
	public Map<String, Object> resAuditRetire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		retireService.resAuditRetire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

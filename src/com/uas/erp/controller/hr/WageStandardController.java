package com.uas.erp.controller.hr;

import com.uas.erp.service.hr.WageStandardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class WageStandardController {

	@Autowired
	private WageStandardService wageStandardService;

	/**
	 * 保存
	 */
	@RequestMapping("/hr/wage/saveWageStandard.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.saveWageStandard(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/wage/updateWageStandard.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.updateWageStandardById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/wage/deleteWageStandard.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.deleteWageStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/hr/wage/WageStandardSet.action")
	@ResponseBody
	public Map<String, Object> setEmpWageStandard(String caller, int wsid,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.setEmpWageStandard(wsid, condition,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/wage/submitWageStandard.action")
	@ResponseBody
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.submitWageStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/wage/resSubmitWageStandard.action")
	@ResponseBody
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.resSubmitWageStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/wage/auditWageStandard.action")
	@ResponseBody
	public Map<String, Object> auditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.auditWageStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/wage/resAuditWageStandard.action")
	@ResponseBody
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.resAuditWageStandard(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 佣金计算
	 */
	@RequestMapping("/hr/wage/PayAccount.action")
	@ResponseBody
	public Map<String, Object> wageAccount(String caller, Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		wageStandardService.payAccount(date, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReceivableService;

@Controller
public class ReceivableController {
	@Autowired
	private ReceivableService ReceivableService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveReceivable.action")
	@ResponseBody
	public Map<String, Object> saveReceivable(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.saveReceivable(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateReceivable.action")
	@ResponseBody
	public Map<String, Object> updateReceivable(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.updateReceivableById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteReceivable.action")
	@ResponseBody
	public Map<String, Object> deleteReceivable(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.deleteReceivable(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitReceivable.action")
	@ResponseBody
	public Map<String, Object> submitReceivable(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.submitReceivable(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitReceivable.action")
	@ResponseBody
	public Map<String, Object> resSubmitReceivable(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.resSubmitReceivable(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditReceivable.action")
	@ResponseBody
	public Map<String, Object> auditReceivable(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.auditReceivable(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditReceivable.action")
	@ResponseBody
	public Map<String, Object> resAuditReceivable(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReceivableService.resAuditReceivable(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 */
	@RequestMapping("/fa/ReceivableController/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = ReceivableService.turnBankRegister(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}
}
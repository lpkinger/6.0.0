package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ReturnService;

@Controller
public class ReturnController {
	@Autowired
	private ReturnService ReturnService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveReturn.action")
	@ResponseBody
	public Map<String, Object> saveReturn(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.saveReturn(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateReturn.action")
	@ResponseBody
	public Map<String, Object> updateReturn(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.updateReturnById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteReturn.action")
	@ResponseBody
	public Map<String, Object> deleteReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.deleteReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitReturn.action")
	@ResponseBody
	public Map<String, Object> submitReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.submitReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitReturn.action")
	@ResponseBody
	public Map<String, Object> resSubmitReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.resSubmitReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditReturn.action")
	@ResponseBody
	public Map<String, Object> auditReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.auditReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditReturn.action")
	@ResponseBody
	public Map<String, Object> resAuditReturn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ReturnService.resAuditReturn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 */
	@RequestMapping("/fa/ReturnController/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = ReturnService.turnBankRegister(caller, data);
		modelMap.put("log", log);
		modelMap.put("success", true);
		return modelMap;
	}

}
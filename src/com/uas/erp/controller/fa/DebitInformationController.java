package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.DebitInformationService;

@Controller
public class DebitInformationController {
	@Autowired
	private DebitInformationService DebitInformationService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveDebitInformation.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.saveDebitInformation(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateDebitInformation.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.updateDebitInformationById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteDebitInformation.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.deleteDebitInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitDebitInformation.action")
	@ResponseBody
	public Map<String, Object> submitDebitInformation(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.submitDebitInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitDebitInformation.action")
	@ResponseBody
	public Map<String, Object> resSubmitDebitInformation(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.resSubmitDebitInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditDebitInformation.action")
	@ResponseBody
	public Map<String, Object> auditDebitInformation(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.auditDebitInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditDebitInformation.action")
	@ResponseBody
	public Map<String, Object> resAuditDebitInformation(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DebitInformationService.resAuditDebitInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
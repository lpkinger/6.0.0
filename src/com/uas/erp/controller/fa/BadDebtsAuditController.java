package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.BadDebtsAuditService;

@Controller
public class BadDebtsAuditController {
	@Autowired
	private BadDebtsAuditService BadDebtsAuditService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/fp/saveBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.saveBadDebtsAudit(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.updateBadDebtsAuditById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.deleteBadDebtsAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> submitBadDebtsAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.submitBadDebtsAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> resSubmitBadDebtsAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.resSubmitBadDebtsAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> auditBadDebtsAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.auditBadDebtsAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> resAuditBadDebtsAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		BadDebtsAuditService.resAuditBadDebtsAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转冲应收款单
	 */
	@RequestMapping("fa/BadDebtsAuditController/turnRecBalanceIMER.action")
	@ResponseBody
	public Map<String, Object> turnRecBalanceIMRE(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int rbid = BadDebtsAuditService.turnRecBalanceIMRE(id, caller);
		modelMap.put("success", true);
		modelMap.put("id", rbid);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/fp/printBadDebtsAudit.action")
	@ResponseBody
	public Map<String, Object> printBadDebtsAudit(int id, String reportName,
			String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = BadDebtsAuditService.printBadDebtsAudit(id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
}
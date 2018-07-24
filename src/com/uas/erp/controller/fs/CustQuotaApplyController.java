package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustQuotaApplyService;

@Controller
public class CustQuotaApplyController {

	@Autowired
	private CustQuotaApplyService custQuotaApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.saveCustQuotaApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.updateCustQuotaApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.deleteCustQuotaApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> submitcustQuotaApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.submitCustQuotaApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitcustQuotaApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.resSubmitCustQuotaApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> auditcustQuotaApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.auditCustQuotaApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditCustQuotaApply.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.resAuditCustQuotaApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存买方额度调查报告-基本资料
	 */
	@RequestMapping("/fs/cust/saveHXSurveyBase.action")
	@ResponseBody
	public Map<String, Object> saveHXSurveyBase(String caller, String formStore, String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.saveHXSurveyBase(caller, formStore, param1, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存买方额度调查报告-经营情况
	 */
	@RequestMapping("/fs/cust/saveHXBusinessCondition.action")
	@ResponseBody
	public Map<String, Object> saveHXBusinessCondition(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.saveHXBusinessCondition(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存买方额度调查报告-财务情况
	 */
	@RequestMapping("/fs/cust/saveHXFinancCondition.action")
	@ResponseBody
	public Map<String, Object> saveHXFinancCondition(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custQuotaApplyService.saveHXFinancCondition(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}

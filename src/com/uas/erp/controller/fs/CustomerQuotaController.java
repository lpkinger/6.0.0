package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CustomerQuotaService;

@Controller
public class CustomerQuotaController {

	@Autowired
	private CustomerQuotaService customerQuotaService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveCustomerQuota(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.updateCustomerQuota(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.deleteCustomerQuota(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> submitCustomerQuota(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.submitCustomerQuota(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> resSubmitCustomerQuota(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.resSubmitCustomerQuota(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> auditCustomerQuota(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.auditCustomerQuota(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditCustomerQuota.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.resAuditCustomerQuota(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取项目风控报告默认值
	 */
	@RequestMapping("/fs/cust/getDefaultDatas.action")
	@ResponseBody
	public Map<String, Object> getDefaultDatas(int cqid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.getDefaultDatas(cqid);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告基本情况
	 */
	@RequestMapping("/fs/cust/saveSurveyBase.action")
	@ResponseBody
	public Map<String, Object> saveSurveyBase(String caller, String formStore, String param1, String param2, String param3, String param4) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveSurveyBase(caller, formStore, param1, param2, param3, param4);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存租赁项目风控报告基本情况
	 */
	@RequestMapping("/fs/cust/saveSurveyBaseZL.action")
	@ResponseBody
	public Map<String, Object> saveSurveyBaseZL(String caller, String formStore, String param1, String param2, String param3) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveSurveyBaseZL(caller, formStore, param1, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告资信情况分析
	 */
	@RequestMapping("/fs/cust/saveCreditStatus.action")
	@ResponseBody
	public Map<String, Object> saveCreditStatus(String caller, String formStore, String param1, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveCreditStatus(caller, formStore, param1, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告收入及盈利情况核实
	 */
	@RequestMapping("/fs/cust/saveIncomeProfit.action")
	@ResponseBody
	public Map<String, Object> saveIncomeProfit(String caller, String formStore, String param1, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveIncomeProfit(caller, formStore, param1, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告调查结论
	 */
	@RequestMapping("/fs/cust/saveSurveyConclusion.action")
	@ResponseBody
	public Map<String, Object> saveSurveyConclusion(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveSurveyConclusion(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告财务报表总体分析
	 */
	@RequestMapping("/fs/cust/saveFaReportAnalysis.action")
	@ResponseBody
	public Map<String, Object> saveFaReportAnalysis(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveFaReportAnalysis(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 保存项目风控报告财务担保情况
	 */
	@RequestMapping("/fs/cust/saveGuarantee.action")
	@ResponseBody
	public Map<String, Object> saveGuarantee(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveGuarantee(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 买方客户维护
	 */
	@RequestMapping("/fs/cust/saveMFCustInfo.action")
	@ResponseBody
	public Map<String, Object> saveMFCustInfo(String gridStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		customerQuotaService.saveMFCustInfo(gridStore);
		modelMap.put("success", true);
		return modelMap;
	}

}

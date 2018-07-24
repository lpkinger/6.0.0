package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.core.FlexJsonUtil;
import com.uas.erp.service.fs.ApiForApplicantService;

@Controller
public class ApiForApplicantController extends BaseController {

	@Autowired
	private ApiForApplicantService apiForApplicantService;
	
	/**
	 * 判断密钥是否存在
	 */
	@RequestMapping("/openapi/applicant/existSecret.action")
	@ResponseBody
	public Map<String, Object> existSecret(String custname) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("existSecret", apiForApplicantService.existSecret(custname));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保理融资申请(ERP)
	 */
	@RequestMapping("/openapi/applicant/assessFinancingApply.action")
	@ResponseBody
	public Map<String, Object> assessFinancingApply(String FinancingApply,Integer year,String yearmonths) {
		Map<String, Object> modelMap = apiForApplicantService.assessFinancingApply(FinancingApply,year,yearmonths);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保理融资申请(B2B)
	 */
	@RequestMapping("/openapi/applicant/financingApply.action")
	@ResponseBody
	public Map<String, Object> FinancingApply(String apply, String customer, String attaches, String customerExcutive, String shareholders, 
			String associateCompany, String changeInstruction, String mfCust, String businessCondition, String prouductMixe, 
			String updowncast, String financeCondition, String accountList) {
		Map<String, Object> modelMap = apiForApplicantService.financingApply(apply, customer, attaches, customerExcutive, shareholders, 
			associateCompany, changeInstruction, mfCust, businessCondition, prouductMixe, updowncast, financeCondition, accountList);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 应收账款转让(产生基础合同)
	 */
	@RequestMapping("/openapi/applicant/AssignRecBalance.action")
	@ResponseBody
	public Map<String, Object> Assign(String cqcode, String sales,String custcode,String custname) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apiForApplicantService.AssignRecBalance(cqcode, sales, custcode, custname);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取保理申请进度
	 */
	@RequestMapping("/openapi/applicant/FinancApplyProgress.action")  
	@ResponseBody 
	public Map<String, Object> FinancApplyProgress(String busincode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("progress", FlexJsonUtil.toJsonArray(apiForApplicantService.FinancApplyProgress(busincode)));
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 应收账款转让（B2B）
	 */
	@RequestMapping("/openapi/applicant/AccountApplyFromB2B.action")  
	@ResponseBody 
	public Map<String, Object> AccountApplyFromB2B(String apply, Long faid, String custname, String fsSales, String receipts, String attaches) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apiForApplicantService.AccountApplyFromB2B(apply, faid, custname, fsSales, receipts, attaches);
		modelMap.put("success", true);
		return modelMap;
	}	

}

package com.uas.erp.controller.b2b;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2b.CustSendSampleService;

@Controller
public class CustSendSampleController {

	@Autowired
	private CustSendSampleService custSendSampleService;
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/b2b/product/updateCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,  String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custSendSampleService.updateCustSendSample(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/b2b/product/submitCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> submitCustSendSample(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custSendSampleService.submitCustSendSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/b2b/product/resSubmitCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitCustSendSample(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custSendSampleService.resSubmitCustSendSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/b2b/product/auditCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> auditQuotationDown(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custSendSampleService.auditCustSendSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/b2b/product/resAuditCustSendSample.action")  
	@ResponseBody 
	public Map<String, Object> resAuditInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custSendSampleService.resAuditCustSendSample(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	@RequestMapping("/b2b/product/CustSendToProdInout.action")
	@ResponseBody
	public Map<String, Object> CustSendToProdInout(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pi_id = custSendSampleService.CustSendToProdInout(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("id", pi_id);
		return modelMap;
	}

	@RequestMapping("/b2b/product/CustSendToSaleInout.action")
	@ResponseBody
	public Map<String, Object> CustSendToPurInout(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int pi_id = custSendSampleService.CustSendToPurInout(formStore, param, caller);
		modelMap.put("success", true);
		modelMap.put("id", pi_id);
		return modelMap;
	}
}

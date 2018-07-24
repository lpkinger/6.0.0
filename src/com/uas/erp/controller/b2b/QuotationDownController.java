package com.uas.erp.controller.b2b;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.b2b.QuotationDownService;

@Controller
public class QuotationDownController {
	@Autowired
	private QuotationDownService quotationDownService;
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/b2b/sale/updateQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.updateQuotationDown(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/b2b/sale/submitQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> submitQuotationDown(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.submitQuotationDowny(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/b2b/sale/resSubmitQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitQuotationDown(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.resSubmitQuotationDown(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/b2b/sale/auditQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> auditQuotationDown(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.auditQuotationDown(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/b2b/sale/resAuditQuotationDown.action")  
	@ResponseBody 
	public Map<String, Object> resAuditInquiry(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.resAuditQuotationDown(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细数据
	 */
	@RequestMapping("/b2b/sale/deleteQuotationDownDetail.action")  
	@ResponseBody 
	public Map<String, Object> deleteQuotationDownDetail(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationDownService.deleteQuotationDownDetail(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

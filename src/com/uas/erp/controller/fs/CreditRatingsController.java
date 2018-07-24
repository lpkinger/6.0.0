package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CreditRatingsService;



@Controller
public class CreditRatingsController {
	@Autowired
	private CreditRatingsService creditRatingsService;
	/**
	 * 保存
	 */
	@RequestMapping("/fs/credit/saveCreditRatings.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.saveCreditRatings(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/fs/credit/updateCreditRatings.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.updateCreditRatings(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/fs/credit/deleteCreditRatings.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.deleteCreditRatings(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/credit/submitCreditRatings.action")
	@ResponseBody
	public Map<String, Object> submitCreditRatings(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.submitCreditRatings(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/credit/resSubmitCreditRatings.action")
	@ResponseBody
	public Map<String, Object> resSubmitCreditRatings(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.resSubmitCreditRatings(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/fs/credit/auditCreditRatings.action")  
	@ResponseBody 
	public Map<String, Object> auditCreditRatings(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.auditCreditRatings(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/fs/credit/resAuditCreditRatings.action")  
	@ResponseBody 
	public Map<String, Object> resAuditCreditRatings(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditRatingsService.resAuditCreditRatings(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

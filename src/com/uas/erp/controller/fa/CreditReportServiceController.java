package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CreditReportServiceService;

@Controller
public class CreditReportServiceController {
	@Autowired
	private CreditReportServiceService CreditReportServiceService;
	/**
	 * 保存CreditReportService
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.saveCreditReportService(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/fa/fp/updateCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.updateCreditReportService(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除CreditReportService
	 */
	@RequestMapping("/fa/fp/deleteCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.deleteCreditReportService(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核CreditReportService
	 */
	@RequestMapping("/fa/fp/auditCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.auditCreditReportService(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核CreditReportService
	 */
	@RequestMapping("/fa/fp/resAuditCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.resAuditCreditReportService(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交CreditReportService
	 */
	@RequestMapping("/fa/fp/submitCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.submitCreditReportService(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交CreditReportService
	 */
	@RequestMapping("/fa/fp/resSubmitCreditReportService.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditReportServiceService.resSubmitCreditReportService(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

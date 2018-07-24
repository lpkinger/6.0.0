package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.DelegationLetterService;

@Controller
public class DelegationLetterController {
	@Autowired
	private DelegationLetterService DelegationLetterService;
	/**
	 * 保存DelegationLetter
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.saveDelegationLetter(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/fa/fp/updateDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.updateDelegationLetter(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除DelegationLetter
	 */
	@RequestMapping("/fa/fp/deleteDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.deleteDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交DelegationLetter
	 */
	@RequestMapping("/fa/fp/submitDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.submitDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交DelegationLetter
	 */
	@RequestMapping("/fa/fp/resSubmitDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.resSubmitDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核DelegationLetter
	 */
	@RequestMapping("/fa/fp/auditDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.auditDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核DelegationLetter
	 */
	@RequestMapping("/fa/fp/resAuditDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.resAuditDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案DelegationLetter
	 */
	@RequestMapping("/fa/fp/endDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> end(int id,String endreason,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.endDelegationLetter(id, endreason,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案DelegationLetter
	 */
	@RequestMapping("/fa/fp/resEndDelegationLetter.action")  
	@ResponseBody 
	public Map<String, Object> resEnd(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		DelegationLetterService.resEndDelegationLetter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/fa/fp/printReceiptDelegationLetter.action")
	@ResponseBody
	public Map<String, Object> printReceipt(HttpSession session, int id,
			String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = DelegationLetterService.printReceiptDelegationLetter(id, caller, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	
}

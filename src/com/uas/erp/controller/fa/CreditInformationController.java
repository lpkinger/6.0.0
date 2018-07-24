package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.fa.CreditInformationService;

@Controller
public class CreditInformationController {
	@Autowired
	private CreditInformationService CreditInformationService;

	/**
	 * 保存CreditInformation
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/fp/saveCreditInformation.action")
	@ResponseBody
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.saveCreditInformation(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/fp/updateCreditInformation.action")
	@ResponseBody
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.updateCreditInformationById(formStore,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/fp/deleteCreditInformation.action")
	@ResponseBody
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.deleteCreditInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/fp/submitCreditInformation.action")
	@ResponseBody
	public Map<String, Object> submitCreditInformation( int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.submitCreditInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/fp/resSubmitCreditInformation.action")
	@ResponseBody
	public Map<String, Object> resSubmitCreditInformation(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.resSubmitCreditInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/fp/auditCreditInformation.action")
	@ResponseBody
	public Map<String, Object> auditCreditInformation(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.auditCreditInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/fp/resAuditCreditInformation.action")
	@ResponseBody
	public Map<String, Object> resAuditCreditInformation(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CreditInformationService.resAuditCreditInformation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
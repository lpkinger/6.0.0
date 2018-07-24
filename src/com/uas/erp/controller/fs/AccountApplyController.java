package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.AccountApplyService;

@Controller
public class AccountApplyController extends BaseController {

	@Autowired
	private AccountApplyService accountApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveAccountApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param1, String param2, String param3, String param4, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.saveAccountApply(formStore, param1, param2, param3, param4, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateAccountApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param1, String param2, String param3, String param4, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.updateAccountApply(formStore, param1, param2, param3, param4, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteAccountApply.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.deleteAccountApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitAccountApply.action")
	@ResponseBody
	public Map<String, Object> submitAccountApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.submitAccountApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitAccountApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitAccountApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.resSubmitAccountApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditAccountApply.action")
	@ResponseBody
	public Map<String, Object> auditAccountApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.auditAccountApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditAccountApply.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.resAuditAccountApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转银行登记
	 */
	@RequestMapping("/fs/accountApplyController/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(String caller, int ar_id) {
		return success(accountApplyService.turnBankRegister(caller, ar_id));
	}

	/**
	 * 删除逾期单
	 */
	@RequestMapping("/fs/buss/deleteFsOverdue.action")
	@ResponseBody
	public Map<String, Object> deleteFsOverdue(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountApplyService.deleteFsOverdue(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

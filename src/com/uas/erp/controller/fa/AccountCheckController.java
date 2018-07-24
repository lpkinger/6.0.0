package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.AccountCheckService;

@Controller
public class AccountCheckController extends BaseController {
	@Autowired
	private AccountCheckService accountCheckService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/saveAccountCheck.action")
	@ResponseBody
	public Map<String, Object> saveAccountCheck(HttpSession session, String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.saveAccountCheck(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/gs/deleteAccountCheck.action")
	@ResponseBody
	public Map<String, Object> deleteAccountCheck(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.deleteAccountCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/gs/updateAccountCheck.action")
	@ResponseBody
	public Map<String, Object> updateAccountCheck(HttpSession session, String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.updateAccountCheck(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/gs/submitAccountCheck.action")
	@ResponseBody
	public Map<String, Object> submitAccountCheck(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.submitAccountCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/gs/resSubmitAccountCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitAccountCheck(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.resSubmitAccountCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/gs/auditAccountCheck.action")
	@ResponseBody
	public Map<String, Object> auditAccountCheck(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.auditAccountCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/gs/resAuditAccountCheck.action")
	@ResponseBody
	public Map<String, Object> resAuditAccountCheck(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.resAuditAccountCheck(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行对账作业取数
	 */
	@RequestMapping("/fa/gs/getBankReconciliation.action")
	@ResponseBody
	public Map<String, Object> getBankReconciliation(HttpSession session, String caller, int yearmonth, String status, String catecode) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data1", accountCheckService.getAccountCheck(caller, yearmonth, status, catecode));
		modelMap.put("data2", accountCheckService.getAccountRegister(caller, yearmonth, status, catecode));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行对账作业自动对账
	 */
	@RequestMapping("/fa/gs/autoCheck.action")
	@ResponseBody
	public Map<String, Object> autoCheck(HttpSession session, String caller, int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.autoCheck(yearmonth, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行对账作业确认对账
	 */
	@RequestMapping(value = "/fa/gs/confirmCheck.action")
	@ResponseBody
	public Map<String, Object> confirmCheck(String data1, String data2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.confirmCheck(data1, data2);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行对账作业取消对账
	 */
	@RequestMapping(value = "/fa/gs/cancelCheck.action")
	@ResponseBody
	public Map<String, Object> cancelCheck(String data1, String data2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountCheckService.cancelCheck(data1, data2);
		modelMap.put("success", true);
		return modelMap;
	}

}

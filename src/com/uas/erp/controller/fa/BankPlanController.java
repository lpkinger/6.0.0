package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.BankPlanService;

@Controller("bankPlanController")
public class BankPlanController extends BaseController {
	@Autowired
	private BankPlanService bankPlanService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/bg/saveBankPlan.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.saveBankPlan(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/bg/deleteBankPlan.action")
	@ResponseBody
	public Map<String, Object> deleteAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.deleteBankPlan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/bg/updateBankPlan.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String param2, String param3, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.updateBankPlanById(caller, formStore, param, param2,
				param3);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/bg/printBankPlan.action")
	@ResponseBody
	public Map<String, Object> printAPBill(HttpSession session, int id,
			String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = bankPlanService.printBankPlan(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * @param session
	 * @param param
	 * @return
	 */
	@RequestMapping("/fa/bg/beforeUpdateBankPlanl.action")
	@ResponseBody
	public Map<String, Object> beforeUpdateAPBill(HttpSession session,
			String params) {
		Map<String, Object> modelMap = new HashMap<String, Object>();

		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/bg/submitBankPlan.action")
	@ResponseBody
	public Map<String, Object> submitAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.submitBankPlan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/bg/resSubmitBankPlan.action")
	@ResponseBody
	public Map<String, Object> resSubmitAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.resSubmitBankPlan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/bg/auditBankPlan.action")
	@ResponseBody
	public Map<String, Object> auditAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.auditBankPlan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/bg/resAuditBankPlan.action")
	@ResponseBody
	public Map<String, Object> resAuditAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		bankPlanService.resAuditBankPlan(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

}

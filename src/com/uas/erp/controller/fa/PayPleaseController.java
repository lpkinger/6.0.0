package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.PayPleaseService;

@Controller
public class PayPleaseController extends BaseController {
	@Autowired
	private PayPleaseService payPleaseService;

	@RequestMapping("/fa/PayPleaseController/savePayPlease.action")
	@ResponseBody
	public Map<String, Object> savePayPlease(HttpSession session, String caller, String formStore, String param, String param2) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.savePayPlease(caller, formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/deletePayPlease.action")
	@ResponseBody
	public Map<String, Object> deletePayPlease(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.deletePayPlease(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/updatePayPlease.action")
	@ResponseBody
	public Map<String, Object> updatePayPlease(HttpSession session, String caller, String formStore, String param, String param2) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.updatePayPleaseById(caller, formStore, param, param2);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/submitPayPlease.action")
	@ResponseBody
	public Map<String, Object> submitPayPlease(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.submitPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/resSubmitPayPlease.action")
	@ResponseBody
	public Map<String, Object> resSubmitPayPlease(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.resSubmitPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/auditPayPlease.action")
	@ResponseBody
	public Map<String, Object> auditPayPlease(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.auditPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/resAuditPayPlease.action")
	@ResponseBody
	public Map<String, Object> resAuditPayPlease(HttpSession session, String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.resAuditPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/turnPrePay.action")
	@ResponseBody
	public Map<String, Object> turnPrePay(String caller, String formStore) {
		return success(payPleaseService.turnPrePay(caller, formStore));
	}

	@RequestMapping("/fa/PayPleaseController/catchAP.action")
	@ResponseBody
	public Map<String, Object> catchAP(HttpSession session, String caller, String ppd_id, String ppd_ppid, String startdate,
			String enddate, String bicode) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.catchAP(caller, ppd_id, ppd_ppid, startdate, enddate, bicode);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PayPleaseController/cleanAP.action")
	@ResponseBody
	public Map<String, Object> cleanAP(HttpSession session, String caller, String ppd_id, String ppd_ppid) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.cleanAP(caller, ppd_id, ppd_ppid);
		modelMap.put("success", true);
		return modelMap;
	}

	// 转银行登记
	@RequestMapping("/fa/PayPleaseController/turnBankRegister.action")
	@ResponseBody
	public Map<String, Object> turnBankRegister(String caller, String formStore) {
		return success(payPleaseService.turnBankRegister(caller, formStore));
	}

	// 转应付票据
	@RequestMapping("/fa/PayPleaseController/turnBillAP.action")
	@ResponseBody
	public Map<String, Object> turnBillAP(String caller, String formStore) {
		return success(payPleaseService.turnBillAP(caller, formStore));
	}

	// 转应收票据异动背书转让
	@RequestMapping("/fa/PayPleaseController/turnBillARChange.action")
	@ResponseBody
	public Map<String, Object> turnBillARChange(String caller, String formStore) {
		return success(payPleaseService.turnBillARChange(caller, formStore));
	}

	// 转冲应付款单
	@RequestMapping("/fa/PayPleaseController/turnPayBalanceCYF.action")
	@ResponseBody
	public Map<String, Object> turnPayBalanceCYF(String caller, String formStore) {
		return success(payPleaseService.turnPayBalanceCYF(caller, formStore));
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/PayPleaseController/printPayPlease.action")
	@ResponseBody
	public Map<String, Object> printPayPlease(HttpSession session, int id, String reportName, String condition, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = payPleaseService.printPayPlease(id, reportName, condition, caller);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/fa/arp/endPayPlease.action")
	@ResponseBody
	public Map<String, Object> endPayPlease(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.endPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/fa/arp/resEndPayPlease.action")
	@ResponseBody
	public Map<String, Object> resEndPayPlease(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.resEndPayPlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除明细时，还原明细发票ab_lockamount
	 */
	@RequestMapping("/fa/arp/reLockAmount.action")
	@ResponseBody
	public Map<String, Object> reLockAmount(int id, String abcode, Double amount) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		payPleaseService.reLockAmount(id, abcode, amount);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.PaymentsDetailArpService;

@Controller
public class PaymentsDetailArpController extends BaseController {
	@Autowired
	private PaymentsDetailArpService paymentsDetailArpService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/arp/savePaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> save(String caller, HttpSession session,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.savePayments(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/arp/deletePaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> deletePayments(String caller,
			HttpSession session, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.deletePayments(caller, id);
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
	@RequestMapping("/fa/arp/updatePaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> update(String caller, HttpSession session,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.updatePaymentsById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核Payments
	 */
	@RequestMapping("/fa/arp/auditPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, HttpSession session, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.auditPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核Payments
	 */
	@RequestMapping("/fa/arp/resAuditPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, HttpSession session,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.resAuditPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交Payments
	 */
	@RequestMapping("/fa/arp/submitPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, HttpSession session, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.submitPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交Payments
	 */
	@RequestMapping("/fa/arp/resSubmitPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, HttpSession session,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.resSubmitPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 禁用Payments
	 */
	@RequestMapping("/fa/arp/bannedPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> banned(String caller, HttpSession session, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.bannedPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用Payments
	 */
	@RequestMapping("/fa/arp/resBannedPaymentsDetail.action")
	@ResponseBody
	public Map<String, Object> resBanned(String caller, HttpSession session,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		paymentsDetailArpService.resBannedPayments(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}

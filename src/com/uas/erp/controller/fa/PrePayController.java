package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.PrePayService;

@Controller
public class PrePayController {
	@Autowired
	private PrePayService prePayService;

	@RequestMapping("/fa/PrePayController/savePrePay.action")
	@ResponseBody
	public Map<String, Object> savePrePay(HttpSession session, String caller,
			String formStore, String param, String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.savePrePay(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/deletePrePay.action")
	@ResponseBody
	public Map<String, Object> deletePrePay(HttpSession session, String caller,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.deletePrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/updatePrePay.action")
	@ResponseBody
	public Map<String, Object> updatePrePay(HttpSession session, String caller,
			String formStore, String param, String param2, String param3) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService
				.updatePrePayById(caller, formStore, param, param2, param3);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/submitPrePay.action")
	@ResponseBody
	public Map<String, Object> submitPrePay(HttpSession session, String caller,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.submitPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/resSubmitPrePay.action")
	@ResponseBody
	public Map<String, Object> resSubmitPrePay(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.resSubmitPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/auditPrePay.action")
	@ResponseBody
	public Map<String, Object> auditPrePay(HttpSession session, String caller,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.auditPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/resAuditPrePay.action")
	@ResponseBody
	public Map<String, Object> resAuditPrePay(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.resAuditPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/postPrePay.action")
	@ResponseBody
	public Map<String, Object> postPrePay(HttpSession session, String caller,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.postPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/PrePayController/resPostPrePay.action")
	@ResponseBody
	public Map<String, Object> resPostPrePay(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		prePayService.resPostPrePay(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/PrePayController/printPrePay.action")
	@ResponseBody
	public Map<String, Object> printPurchase(HttpSession session, int id,
			String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = prePayService.printPrePay(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

}

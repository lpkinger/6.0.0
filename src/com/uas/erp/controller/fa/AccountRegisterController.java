package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AccountRegisterService;

@Controller
public class AccountRegisterController {
	@Autowired
	private AccountRegisterService accountRegisterService;

	@RequestMapping("/fa/ars/saveAccountRegister.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String caller,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.saveAccountRegister(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/deleteAccountRegister.action")
	@ResponseBody
	public Map<String, Object> deleteAccountRegister(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.deleteAccountRegister(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/updateAccountRegister.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String caller,
			String formStore, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.updateAccountRegisterById(caller, formStore,
				param);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/submitAccountRegister.action")
	@ResponseBody
	public Map<String, Object> submitAccountRegister(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.submitAccountRegister(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resSubmitAccountRegister.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBill(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.resSubmitAccountRegister(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/auditAccountRegister.action")
	@ResponseBody
	public Map<String, Object> auditARBill(HttpSession session, String caller,
			int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.auditAccountRegister(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resAuditAccountRegister.action")
	@ResponseBody
	public Map<String, Object> resAuditARBill(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountRegisterService.resAuditAccountRegister(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/postAccountRegister.action")
	@ResponseBody
	public Map<String, Object> postAccountRegister(HttpSession session,
			String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		// accountRegisterService.resAuditAccountRegister(id, language,
		// employee);
		modelMap.put("success", true);
		return modelMap;
	}

}

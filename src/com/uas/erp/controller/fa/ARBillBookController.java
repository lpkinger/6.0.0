package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ARBillBookService;

@Controller
public class ARBillBookController {
	@Autowired
	private ARBillBookService aRBillBookService;

	@RequestMapping("/fa/ars/saveARBillBook.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.saveARBillBook(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/deleteARBillBook.action")
	@ResponseBody
	public Map<String, Object> deleteARBillBook(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.deleteARBillBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/updateARBillBook.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.updateARBillBookById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/submitARBillBook.action")
	@ResponseBody
	public Map<String, Object> submitARBillBook(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.submitARBillBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resSubmitARBillBook.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.resSubmitARBillBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/auditARBillBook.action")
	@ResponseBody
	public Map<String, Object> auditARBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.auditARBillBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/fa/ars/resAuditARBillBook.action")
	@ResponseBody
	public Map<String, Object> resAuditARBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		aRBillBookService.resAuditARBillBook(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

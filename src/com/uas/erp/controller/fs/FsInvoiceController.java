package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.FsInvoiceService;

@Controller
public class FsInvoiceController {

	@Autowired
	private FsInvoiceService fsInvoiceService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/cust/saveFsInvoice.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.saveFsInvoice(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/cust/updateFsInvoice.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.updateFsInvoice(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/cust/deleteFsInvoice.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.deleteFsInvoice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/cust/submitFsInvoice.action")
	@ResponseBody
	public Map<String, Object> submitFsInvoice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.submitFsInvoice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/cust/resSubmitFsInvoice.action")
	@ResponseBody
	public Map<String, Object> resSubmitFsInvoice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.resSubmitFsInvoice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/cust/auditFsInvoice.action")
	@ResponseBody
	public Map<String, Object> auditFsInvoice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.auditFsInvoice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/cust/resAuditFsInvoice.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInvoiceService.resAuditFsInvoice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

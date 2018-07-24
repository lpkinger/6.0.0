package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.ReimbursementApplyService;

@Controller
public class ReimbursementApplyController {

	@Autowired
	private ReimbursementApplyService reimbursementApplyService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/buss/saveReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.saveReimbursementApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/buss/updateReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.updateReimbursementApply(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/buss/deleteReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.deleteReimbursementApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/buss/submitReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> submitReimbursementApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.submitReimbursementApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/buss/resSubmitReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitReimbursementApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.resSubmitReimbursementApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/buss/auditReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> auditReimbursementApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.auditReimbursementApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/buss/resAuditReimbursementApply.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		reimbursementApplyService.resAuditReimbursementApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

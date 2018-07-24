package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.FsRepaymentService;

@Controller
public class FsRepaymentController extends BaseController {

	@Autowired
	private FsRepaymentService fsRepaymentService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/buss/saveFsRepayment.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.saveFsRepayment(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/buss/updateFsRepayment.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.updateFsRepayment(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/buss/deleteFsRepayment.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.deleteFsRepayment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/buss/submitFsRepayment.action")
	@ResponseBody
	public Map<String, Object> submitFsRepayment(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.submitFsRepayment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/buss/resSubmitFsRepayment.action")
	@ResponseBody
	public Map<String, Object> resSubmitFsRepayment(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.resSubmitFsRepayment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/buss/auditFsRepayment.action")
	@ResponseBody
	public Map<String, Object> auditFsRepayment(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.auditFsRepayment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/buss/resAuditFsRepayment.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsRepaymentService.resAuditFsRepayment(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

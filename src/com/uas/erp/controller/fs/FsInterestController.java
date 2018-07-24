package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.FsInterestService;

@Controller
public class FsInterestController {

	@Autowired
	private FsInterestService fsInterestService;

	/**
	 * 更改
	 */
	@RequestMapping("/fs/buss/updateFsInterest.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.updateFsInterest(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/buss/deleteFsInterest.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.deleteFsInterest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/buss/submitFsInterest.action")
	@ResponseBody
	public Map<String, Object> submitFsInterest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.submitFsInterest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/buss/resSubmitFsInterest.action")
	@ResponseBody
	public Map<String, Object> resSubmitFsInterest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.resSubmitFsInterest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/buss/auditFsInterest.action")
	@ResponseBody
	public Map<String, Object> auditFsInterest(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.auditFsInterest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/buss/resAuditFsInterest.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsInterestService.resAuditFsInterest(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.FsPoundageInService;

@Controller
public class FsPoundageInController extends BaseController {

	@Autowired
	private FsPoundageInService fsPoundageInService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/buss/saveFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.saveFsPoundageIn(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/buss/updateFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.updateFsPoundageIn(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/buss/deleteFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.deleteFsPoundageIn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/buss/submitFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> submitFsPoundageIn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.submitFsPoundageIn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/buss/resSubmitFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> resSubmitFsPoundageIn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.resSubmitFsPoundageIn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/buss/auditFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> auditFsPoundageIn(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.auditFsPoundageIn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/buss/resAuditFsPoundageIn.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsPoundageInService.resAuditFsPoundageIn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

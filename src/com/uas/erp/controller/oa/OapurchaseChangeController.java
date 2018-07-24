package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OapurchaseChangeService;

@Controller
public class OapurchaseChangeController {
	@Autowired
	private OapurchaseChangeService oapurchaseChangeService;

	// 规范 小写
	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/appliance/saveOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.saveOapurchaseChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/appliance/deleteOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> deleteOapurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.deleteOapurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/oa/appliance/updateOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.updateOapurchaseChangeById(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/appliance/submitOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> submitOapurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.submitOapurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/appliance/resSubmitOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitOapurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.resSubmitOapurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/oa/appliance/auditOapurchaseChange.action")
	@ResponseBody
	public Map<String, Object> auditOapurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oapurchaseChangeService.auditOapurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

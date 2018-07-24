package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.KBIChangeManService;

@Controller
public class KBIChangeManController {
	@Autowired
	private KBIChangeManService kbiChangeManService;

	/**
	 * 保存form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/saveKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.saveKBIChangeMan(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/kbi/deleteKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> deleteKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.deleteKBIChangeMan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form
	 * 
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/hr/kbi/updateKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.updateKBIChangeManById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/kbi/submitKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> submitKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.submitKBIChangeMan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/kbi/resSubmitKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> resSubmitKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.resSubmitKBIChangeMan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/hr/kbi/auditKBIChangeMan.action")
	@ResponseBody
	public Map<String, Object> auditKBIChangeMan(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		kbiChangeManService.auditKBIChangeMan(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

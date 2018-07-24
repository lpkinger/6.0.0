package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.fa.ARBadDebtsOptionService;

@Controller
public class ARBadDebtsOptionController {
	@Autowired
	private ARBadDebtsOptionService arBadDebtsOptionService;

	/**
	 * 保存ARBadDebtsOption
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/fa/ars/saveARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.saveARBadDebtsOption(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fa/ars/updateARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.updateARBadDebtsOptionById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/ars/deleteARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.deleteARBadDebtsOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> submitARBadDebts(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.submitARBadDebtsOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> resSubmitARBadDebts(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.resSubmitARBadDebtsOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> auditARBadDebts(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.auditARBadDebtsOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditARBadDebtsOption.action")
	@ResponseBody
	public Map<String, Object> resAuditARBadDebts(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arBadDebtsOptionService.resAuditARBadDebtsOption(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}
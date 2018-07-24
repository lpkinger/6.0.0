package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PurchaseChangeService;

@Controller
public class PurchaseChangeController extends BaseController {
	@Autowired
	private PurchaseChangeService purchaseChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/savePurchaseChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.savePurchaseChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deletePurchaseChange.action")
	@ResponseBody
	public Map<String, Object> deletePurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.deletePurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/updatePurchaseChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.updatePurchaseChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printPurchaseChange.action")
	@ResponseBody
	public Map<String, Object> printPurchaseChange(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = purchaseChangeService.printPurchaseChange(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitPurchaseChange.action")
	@ResponseBody
	public Map<String, Object> submitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.submitPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitPurchaseChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.resSubmitPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditPurchaseChange.action")
	@ResponseBody
	public Map<String, Object> auditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.auditPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 同意变更
	 */
	@RequestMapping("/scm/purchase/agreePurchaseChange.action")
	@ResponseBody
	public Map<String, Object> agreePurchaseChange(String code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.onChangeAgreed(code);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditPurchaseChange.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchaseChangeService.resAuditPurchaseChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/scm/purchase/change/needcheck.action")
	@ResponseBody
	public void needCheck(String caller, Integer changeId) {
		purchaseChangeService.needCheck(changeId);
	}

}

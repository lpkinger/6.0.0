package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.PurchasePLMService;

@Controller
public class PurchasePLMController extends BaseController {
	@Autowired
	private PurchasePLMService purchasePLMService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/plm/purchase/savePurchase.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.savePurchase(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除试产采购单数据 包括采购明细
	 */
	@RequestMapping("/plm/purchase/deletePurchase.action")
	@ResponseBody
	public Map<String, Object> deletePurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.deletePurchase(id, caller);
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
	@RequestMapping("/plm/purchase/updatePurchase.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.updatePurchaseById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印试产采购单
	 */
	@RequestMapping("/plm/purchase/printPurchase.action")
	@ResponseBody
	public Map<String, Object> printPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.printPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交试产采购单
	 */
	@RequestMapping("/plm/purchase/submitPurchase.action")
	@ResponseBody
	public Map<String, Object> submitPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.submitPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交试产采购单
	 */
	@RequestMapping("/plm/purchase/resSubmitPurchase.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.resSubmitPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核试产采购单
	 */
	@RequestMapping("/plm/purchase/auditPurchase.action")
	@ResponseBody
	public Map<String, Object> auditPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.auditPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核试产采购单
	 */
	@RequestMapping("/plm/purchase/resAuditPurchase.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.resAuditPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 结案
	 */
	@RequestMapping("/plm/purchase/endPurchase.action")
	@ResponseBody
	public Map<String, Object> endPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.endPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反结案
	 */
	@RequestMapping("/plm/purchase/resEndPurchase.action")
	@ResponseBody
	public Map<String, Object> resEndPurchase(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purchasePLMService.resEndPurchase(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

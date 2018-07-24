package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.APCheckService;

@Controller
public class APCheckController extends BaseController {
	@Autowired
	private APCheckService apCheckService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/arp/saveAPCheck.action")
	@ResponseBody
	public Map<String, Object> saveAPCheck(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.saveAPCheck(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/arp/deleteAPCheck.action")
	@ResponseBody
	public Map<String, Object> deleteAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.deleteAPCheck(id);
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
	@RequestMapping("/fa/arp/updateAPCheck.action")
	@ResponseBody
	public Map<String, Object> updateAPCheck(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.updateAPCheckById(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/arp/printAPCheck.action")
	@ResponseBody
	public Map<String, Object> printAPCheck(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = apCheckService.printAPCheck(id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/arp/submitAPCheck.action")
	@ResponseBody
	public Map<String, Object> submitAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.submitAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/arp/resSubmitAPCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.resSubmitAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/arp/auditAPCheck.action")
	@ResponseBody
	public Map<String, Object> auditAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.auditAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/arp/resAuditAPCheck.action")
	@ResponseBody
	public Map<String, Object> resAuditAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.resAuditAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/arp/postAPCheck.action")
	@ResponseBody
	public Map<String, Object> accountAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.accountedAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/arp/resPostAPCheck.action")
	@ResponseBody
	public Map<String, Object> resAccountAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.resAccountedAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认对账
	 */
	@RequestMapping("/fa/arp/confirmAPCheck.action")
	@ResponseBody
	public Map<String, Object> confirmAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.confirmAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消对账
	 */
	@RequestMapping("/fa/arp/cancelAPCheck.action")
	@ResponseBody
	public Map<String, Object> cancelAPCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.cancelAPCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 不同意对账
	 */
	@RequestMapping("/fa/arp/resConfirmAPCheck.action")
	@ResponseBody
	public Map<String, Object> resConfirmAPCheck(int id, String reason) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.resConfirmAPCheck(id, reason);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认开票
	 */
	@RequestMapping("/fa/arp/apCheckTurnBill.action")
	@ResponseBody
	public Map<String, Object> turnBill(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", apCheckService.turnBill(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转付款申请单
	 */
	@RequestMapping("/fa/arp/turnPayPlease.action")
	@ResponseBody
	public Map<String, Object> turnPayPlease(int id, String caller) {
		return success(apCheckService.turnPayPlease(id, caller));
	}

	/**
	 * 提交(确认)
	 * */
	@RequestMapping("/fa/arp/submitAPCheckConfirm.action")
	@ResponseBody
	public Map<String, Object> submitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.submitAPCheckConfirm(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交(确认)
	 * */
	@RequestMapping("/fa/arp/resSubmitAPCheckConfirm.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		apCheckService.resSubmitAPCheckConfirm(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

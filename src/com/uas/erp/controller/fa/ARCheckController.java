package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.ARCheckService;

@Controller
public class ARCheckController extends BaseController {
	@Autowired
	private ARCheckService arCheckService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/saveARCheck.action")
	@ResponseBody
	public Map<String, Object> saveARCheck(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.saveARCheck(formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/fa/ars/deleteARCheck.action")
	@ResponseBody
	public Map<String, Object> deleteARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.deleteARCheck(id);
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
	@RequestMapping("/fa/ars/updateARCheck.action")
	@ResponseBody
	public Map<String, Object> updateARCheck(String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.updateARCheckById(formStore, param);
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
	@RequestMapping("/fa/ars/saveDetailInfo.action")
	@ResponseBody
	public Map<String, Object> updateDetailInfo(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.updateDetailInfo(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/ars/printARCheck.action")
	@ResponseBody
	public Map<String, Object> printARCheck(int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = arCheckService.printARCheck(id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/fa/ars/submitARCheck.action")
	@ResponseBody
	public Map<String, Object> submitARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.submitARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/fa/ars/resSubmitARCheck.action")
	@ResponseBody
	public Map<String, Object> resSubmitARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.resSubmitARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fa/ars/auditARCheck.action")
	@ResponseBody
	public Map<String, Object> auditARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.auditARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fa/ars/resAuditARCheck.action")
	@ResponseBody
	public Map<String, Object> resAuditARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.resAuditARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 记账
	 */
	@RequestMapping("/fa/ars/postARCheck.action")
	@ResponseBody
	public Map<String, Object> accountARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.accountedARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反记账
	 */
	@RequestMapping("/fa/ars/resPostARCheck.action")
	@ResponseBody
	public Map<String, Object> resAccountARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.resAccountedARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认对账
	 */
	@RequestMapping("/fa/ars/confirmARCheck.action")
	@ResponseBody
	public Map<String, Object> confirmARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.confirmARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消对账
	 */
	@RequestMapping("/fa/ars/cancelARCheck.action")
	@ResponseBody
	public Map<String, Object> cancelARCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.cancelARCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确认开票
	 */
	@RequestMapping("/fa/ars/arCheckTurnBill.action")
	@ResponseBody
	public Map<String, Object> turnBill(String data, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", arCheckService.turnBill(caller, data));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交(确认)
	 * */
	@RequestMapping("/fa/ars/submitARCheckConfirm.action")
	@ResponseBody
	public Map<String, Object> submitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.submitARCheckConfirm(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交(确认)
	 * */
	@RequestMapping("/fa/ars/resSubmitARCheckConfirm.action")
	@ResponseBody
	public Map<String, Object> resSubmitTurnSale(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		arCheckService.resSubmitARCheckConfirm(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转回款通知单
	 */
	@RequestMapping("/fa/ars/turnRecBalanceNotice.action")
	@ResponseBody
	public Map<String, Object> turnRecBalanceNotice(String id,String data, String caller) {
		int ac_id = Integer.parseInt(id);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", arCheckService.turnRecBalanceNotice(ac_id,data, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}

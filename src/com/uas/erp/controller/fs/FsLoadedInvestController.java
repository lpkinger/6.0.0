package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fs.FsLoadedInvestService;

@Controller
public class FsLoadedInvestController extends BaseController {

	@Autowired
	private FsLoadedInvestService fsLoadedInvestService;

	/**
	 * 保存
	 */
	@RequestMapping("/fs/loaded/saveInvestReport.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.saveInvestReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/fs/loaded/updateInvestReport.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.updateInvestReport(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fs/loaded/deleteInvestReport.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.deleteInvestReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/loaded/submitInvestReport.action")
	@ResponseBody
	public Map<String, Object> submitInvestReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.submitInvestReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/loaded/resSubmitInvestReport.action")
	@ResponseBody
	public Map<String, Object> resSubmitInvestReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.resSubmitInvestReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/fs/loaded/auditInvestReport.action")
	@ResponseBody
	public Map<String, Object> auditInvestReport(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.auditInvestReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/fs/loaded/resAuditInvestReport.action")
	@ResponseBody
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.resAuditInvestReport(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取默认值
	 */
	@RequestMapping("/fs/loaded/getDefaultDatas.action")
	@ResponseBody
	public Map<String, Object> getDefault(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.getDefault(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 买卖双方交易检查
	 */
	@RequestMapping("/fs/loaded/updateTransactionCheck.action")
	@ResponseBody
	public Map<String, Object> updateTransactionCheck(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.updateTransactionCheck(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 担保条件检查
	 */
	@RequestMapping("/fs/loaded/updateGuaranteeCheck.action")
	@ResponseBody
	public Map<String, Object> updateGuaranteeCheck(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.updateGuaranteeCheck(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 主要结算账户检查
	 */
	@RequestMapping("/fs/loaded/saveSettleAccountCheck.action")
	@ResponseBody
	public Map<String, Object> saveSettleAccountCheck(String formStore, String param1, String param2, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		fsLoadedInvestService.saveSettleAccountCheck(formStore, param1, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.cost.ContractCostCloseService;

@Controller
public class ContractCostCloseController extends BaseController {
	@Autowired
	private ContractCostCloseService contractCostCloseService;

	@RequestMapping("/co/cost/saveContractCostClose.action")
	@ResponseBody
	public Map<String, Object> saveContractCostClose(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.saveContractCostClose(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/co/cost/deleteContractCostClose.action")
	@ResponseBody
	public Map<String, Object> deleteContractCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.deleteContractCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/co/cost/updateContractCostClose.action")
	@ResponseBody
	public Map<String, Object> updateContractCostClose(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.updateContractCostClose(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/co/cost/submitContractCostClose.action")
	@ResponseBody
	public Map<String, Object> submitContractCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.submitContractCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/co/cost/resSubmitContractCostClose.action")
	@ResponseBody
	public Map<String, Object> resSubmitContractCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.resSubmitContractCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/co/cost/auditContractCostClose.action")
	@ResponseBody
	public Map<String, Object> auditContractCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.auditContractCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/co/cost/resAuditContractCostClose.action")
	@ResponseBody
	public Map<String, Object> resAuditContractCostClose(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.resAuditContractCostClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 生成凭证
	 */
	@RequestMapping("/co/cost/createCostVoucher.action")
	@ResponseBody
	public Map<String, Object> createCostVoucher(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", contractCostCloseService.createCostVoucher(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 取消凭证
	 */
	@RequestMapping("/co/cost/cancelCostVoucher.action")
	@ResponseBody
	public Map<String, Object> cancelCostVoucher(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.cancelCostVoucher(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取合同
	 * 
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/co/cost/catchProjectCost.action")
	@ResponseBody
	public Map<String, Object> catchProjectCost(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.catchProjectCost(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 清除合同
	 * 
	 * @param caller
	 * @param formStore
	 * @return
	 */
	@RequestMapping("/co/cost/cleanProjectCost.action")
	@ResponseBody
	public Map<String, Object> cleanProjectCost(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractCostCloseService.cleanProjectCost(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
}

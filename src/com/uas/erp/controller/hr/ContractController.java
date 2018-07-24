package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.ContractService;

@Controller
public class ContractController {
	@Autowired
	private ContractService contractService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/hr/emplmana/saveContract.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.saveContract(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateContract.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.updateContractById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/hr/emplmana/deleteContract.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.deleteContract(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核Contract
	 */
	@RequestMapping("/hr/emplmana/auditContract.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.auditContract(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核Contract
	 */
	@RequestMapping("/hr/emplmana/resAuditContract.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.resAuditContract(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交Contract
	 */
	@RequestMapping("/hr/emplmana/submitContract.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.submitContract(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交Contract
	 */
	@RequestMapping("/hr/emplmana/resSubmitContract.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractService.resSubmitContract(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

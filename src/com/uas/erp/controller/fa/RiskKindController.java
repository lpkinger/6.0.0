package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.RiskKindService;

@Controller
public class RiskKindController {
	@Autowired
	private RiskKindService RiskKindService;
	/**
	 * 保存RiskKind
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/fa/fp/saveRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.saveRiskKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/fa/fp/updateRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.updateRiskKind(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除RiskKind
	 */
	@RequestMapping("/fa/fp/deleteRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.deleteRiskKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核RiskKind
	 */
	@RequestMapping("/fa/fp/auditRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.auditRiskKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核RiskKind
	 */
	@RequestMapping("/fa/fp/resAuditRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.resAuditRiskKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交RiskKind
	 */
	@RequestMapping("/fa/fp/submitRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.submitRiskKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交RiskKind
	 */
	@RequestMapping("/fa/fp/resSubmitRiskKind.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		RiskKindService.resSubmitRiskKind(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

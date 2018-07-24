package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.ContractRenewService;

@Controller
public class ContractRenewController {
	@Autowired
	private ContractRenewService contractRenewService;
	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditContractRenew.action")  
	@ResponseBody 
	public Map<String, Object> auditTurnfullmemb(String caller, int id) {				
		Map<String, Object> modelMap = new HashMap<String, Object>();
		contractRenewService.audit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

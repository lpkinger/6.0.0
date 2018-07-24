package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.ResourceDistrApplyService;

@Controller
public class ResourceDistrApplyController {
	@Autowired
	private ResourceDistrApplyService resourceDistrApplyService;

	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditResourceDistrApply.action")
	@ResponseBody
	public Map<String, Object> auditResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrApplyService.audit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditResourceDistrApply.action")
	@ResponseBody
	public Map<String, Object> resAuditResearchPlan(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrApplyService.resAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/marketmgr/saveResourceDistrApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrApplyService.saveResourceDistrApply(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/marketmgr/deleteResourceDistrApply.action")
	@ResponseBody
	public Map<String, Object> deleteCustomerDistr(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrApplyService.deleteResourceDistrApply(id, caller);
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
	@RequestMapping("/crm/marketmgr/updateResourceDistrApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		resourceDistrApplyService.updateResourceDistrApply(formStore, param,
				caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

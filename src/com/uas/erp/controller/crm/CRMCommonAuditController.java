package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.CRMCommonAuditService;

@Controller
public class CRMCommonAuditController {
	@Autowired
	private CRMCommonAuditService crmCommonAuditService;

	/**
	 * 审核
	 */
	@RequestMapping("/common/CRMCommonAudit.action")
	@ResponseBody
	public Map<String, Object> auditCommon(String caller, int id,
			String auditerFieldName, String auditdateFieldName) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		crmCommonAuditService.audit(caller, id, auditerFieldName,
				auditdateFieldName);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/common/CRMCommonResAudit.action")
	@ResponseBody
	public Map<String, Object> resAuditCommon(String caller, int id,
			String auditerFieldName, String auditdateFieldName) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		crmCommonAuditService.resAudit(caller, id, auditerFieldName,
				auditdateFieldName);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/common/confirmCommon.action")
	@ResponseBody
	public Map<String, Object> confirmCommon(String caller, int id,
			String auditerFieldName) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		crmCommonAuditService.confirmCommon(caller, id, auditerFieldName);
		modelMap.put("success", true);
		return modelMap;
	}
}

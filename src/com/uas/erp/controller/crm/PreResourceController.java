package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.PreResourceService;

@Controller
public class PreResourceController {
	@Autowired
	private PreResourceService preResourceService;

	/**
	 * 审核
	 */
	@RequestMapping("/crm/auditPreResource.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preResourceService.audit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/resAuditPreResource.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preResourceService.resAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

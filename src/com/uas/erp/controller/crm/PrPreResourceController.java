package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.PrPreResourceService;

@Controller
public class PrPreResourceController {
	@Autowired
	private PrPreResourceService prpreResourceService;

	/**
	 * 审核
	 */
	@RequestMapping("/crm/auditPrPreResource.action")
	@ResponseBody
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prpreResourceService.audit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/resAuditPrPreResource.action")
	@ResponseBody
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prpreResourceService.resAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转资源
	 */
	@RequestMapping("/crm/turnPreResource.action")
	@ResponseBody
	public Map<String, Object> turnCustomer(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cuid = prpreResourceService.turnPreResource(id, caller);
		modelMap.put("id", cuid);
		modelMap.put("success", true);
		return modelMap;
	}

}

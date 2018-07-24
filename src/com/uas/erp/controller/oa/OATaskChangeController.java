package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.OATaskChangeService;

@Controller
public class OATaskChangeController {
	@Autowired
	private OATaskChangeService oaTaskChangeService;

	@RequestMapping("/oa/myProcess/auditOATaskChange.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaTaskChangeService.auditOATaskChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/oa/myProcess/resAuditOATaskChange.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		oaTaskChangeService.resAuditOATaskChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

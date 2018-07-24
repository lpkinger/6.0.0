package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MJProjectChangeService;

@Controller
public class MJProjectChangeController extends BaseController {
	@Autowired
	private MJProjectChangeService MJProjectChangeService;

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditMJProjectChange.action")
	@ResponseBody
	public Map<String, Object> auditMJProjectChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MJProjectChangeService.auditMJProjectChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核模具委托保管书
	 */
	@RequestMapping("/pm/mould/auditMJProject.action")
	@ResponseBody
	public Map<String, Object> auditMJProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MJProjectChangeService.auditMJProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核模具委托保管书
	 */
	@RequestMapping("/pm/mould/resAuditMJProject.action")
	@ResponseBody
	public Map<String, Object> resAuditMJProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		MJProjectChangeService.resAuditMJProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

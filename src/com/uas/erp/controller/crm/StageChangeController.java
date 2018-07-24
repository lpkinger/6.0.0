package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.StageChangeService;

@Controller
public class StageChangeController {
	@Autowired
	private StageChangeService stageService;

	/**
	 * 审核
	 */
	@RequestMapping("/crm/chance/auditStageChange.action")
	@ResponseBody
	public Map<String, Object> auditStageChange(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		stageService.audit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/chance/resAuditStageChange.action")
	@ResponseBody
	public Map<String, Object> resAuditStageChange(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		stageService.resAudit(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

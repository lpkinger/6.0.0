package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RecruitApplyService;

@Controller
public class RecruitApplyController {
	@Autowired
	private RecruitApplyService recruitApplyService;

	/**
	 * 审核
	 */
	@RequestMapping("/hr/emplmana/auditRecruitApply.action")
	@ResponseBody
	public Map<String, Object> auditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitApplyService.auditRecruitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/hr/emplmana/resAuditRecruitApply.action")
	@ResponseBody
	public Map<String, Object> resAuditKBIbill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitApplyService.resAuditRecruitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案
	 */
	@RequestMapping("/hr/emplmana/endRecruitApply.action")  
	@ResponseBody 
	public Map<String, Object> endRecruitApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		recruitApplyService.endRecruitApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

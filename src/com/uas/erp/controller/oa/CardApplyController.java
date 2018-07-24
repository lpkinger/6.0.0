package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.CardApplyService;

@Controller
public class CardApplyController {

	@Autowired
	private CardApplyService cardApplyService;

	/**
	 * 审核
	 */
	@RequestMapping("/oa/check/auditCardApply.action")
	@ResponseBody
	public Map<String, Object> auditCardApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		cardApplyService.auditCardApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

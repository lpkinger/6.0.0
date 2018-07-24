package com.uas.erp.controller.fs;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.FinancingApplyService;

@Controller
public class FinancingApplyController {
	@Autowired
	private FinancingApplyService financingApplyService;

	/**
	 * 获取保理融资申请
	 */
	@RequestMapping("/fs/cust/getFinancingApply.action")
	@ResponseBody
	public Map<String, Object> getFinancingApply(String condition) {
		Map<String, Object> modelMap = financingApplyService.getFinancingApply(condition);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交融资保理申请
	 */
	@RequestMapping("/fs/cust/submitApply.action")
	@ResponseBody
	public Map<String, Object> submitApply(HttpSession session, String formStore, String caller) {
		Map<String, Object> modelMap = financingApplyService.submitApply(session, formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 获取保理申请进度
	 */
	@RequestMapping("/fs/cust/getFinancApplyProgress.action")  
	@ResponseBody 
	public Map<String, Object> getFinancApplyProgress(String condition, String busincode) {
		Map<String, Object> modelMap = financingApplyService.getFinancApplyProgress(condition, busincode);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

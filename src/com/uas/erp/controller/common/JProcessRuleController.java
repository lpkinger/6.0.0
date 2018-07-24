package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.JProcessRuleService;
@Controller
public class JProcessRuleController {
	@Autowired
	JProcessRuleService jProcessRuleService;


	/*
	 * 保存
	 */
	@RequestMapping("/common/saveJprocessRule.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessRuleService.saveJProcessRule(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/common/updateJprocessRule.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String caller, String formStore) {
		String language = (String)session.getAttribute("language");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessRuleService.updateJProcessRule(caller,formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteJprocessRule.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessRuleService.deleteJProcessRule(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 检测sql语句
	 */
	@RequestMapping("/common/checkRuleSql.action")  
	@ResponseBody 
	public Map<String, Object> checkSql(HttpSession session, String sql) {
		Map<String, Object> modelMap = jProcessRuleService.checkSql(sql);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

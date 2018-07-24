package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.RemainQueryService;

@Controller
public class RemainQueryController {
	
	@Autowired
	private RemainQueryService remainQueryService;
	
	/**
	 * 科目余额查询
	 */
	@RequestMapping("/fa/gls/getRemainQuery.action")  
	@ResponseBody 
	public Map<String, Object> getRemainQuery(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", remainQueryService.RemainQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	
}

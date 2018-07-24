package com.uas.api.serve.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.api.serve.service.WisdomParkNewsService;
import com.uas.erp.core.BaseUtil;


@Controller
public class WisdomParkNewsController {
	
	@Autowired
	private WisdomParkNewsService wisdomParkNewsService;
	

	/**
	 * 获取新闻类型
	 */
	@RequestMapping("/api/serve/mainPage/getNewsType.action") 
	@ResponseBody 
	public Map<String, Object> getNewsType(HttpServletRequest request) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list", wisdomParkNewsService.getNewsType(basePath));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取新闻列表
	 */
	@RequestMapping("/api/serve/mainPage/getNewslist.action")  
	@ResponseBody 
	public Map<String, Object> getNewslist(HttpServletRequest request, String type, Integer limit, Integer page) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("total", wisdomParkNewsService.getNewsTotal(type));
		modelMap.put("list", wisdomParkNewsService.getNewslist(basePath, type, limit, page));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 获取新闻内容
	 */
	@RequestMapping("/api/serve/mainPage/getNewsContent.action")  
	@ResponseBody 
	public Map<String, Object> getNewsContent(HttpServletRequest request, Integer id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",wisdomParkNewsService.getNewsContent(request, id));
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.api.serve.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.api.serve.service.WisdomParkActivityService;
import com.uas.erp.core.BaseUtil;


@Controller
public class WisdomParkActivityController {
	
	@Autowired
	private WisdomParkActivityService wisdomParkActivityService;
	

	/**
	 * 获取活动类型
	 */
	@RequestMapping("/api/serve/mainPage/getActivityType.action") 
	@ResponseBody 
	public Map<String, Object> getActivityType(HttpServletRequest request) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("list", wisdomParkActivityService.getActivityType(basePath));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取活动列表
	 */
	@RequestMapping("/api/serve/mainPage/getActivitylist.action")  
	@ResponseBody 
	public Map<String, Object> getActivitylist(HttpServletRequest request, String type, Integer limit, Integer page) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("total", wisdomParkActivityService.getActivityTotal(type));
		modelMap.put("list", wisdomParkActivityService.getActivitylist(basePath, type, limit, page));
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 获取活动内容
	 */
	@RequestMapping("/api/serve/mainPage/getActivityContent.action")  
	@ResponseBody 
	public Map<String, Object> getActivityContent(HttpServletRequest request, Integer id, Long uu) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",wisdomParkActivityService.getActivityContent(basePath, id, uu));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 活动报名
	 */
	@RequestMapping("/api/serve/ActivityRegistration.action")  
	@ResponseBody 
	public Map<String, Object> ActivityRegistration(Integer id, Long uu, String name) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("msg",wisdomParkActivityService.ActivityRegistration(id, uu, name));
		modelMap.put("success", true);
		return modelMap;
	}
}

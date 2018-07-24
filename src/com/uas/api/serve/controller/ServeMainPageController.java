package com.uas.api.serve.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.api.serve.service.ServeMainPageService;
import com.uas.erp.core.BaseUtil;

/**
 * 产城API首页接口的Controller
 * 
 * @author chenrh
 * @since 2017年11月14日 13:39:10
 */

@Controller
public class ServeMainPageController {
	
	@Autowired
	private ServeMainPageService serveMainPageService;
	
	/**
	 * 获取所有服务信息
	 */
	@RequestMapping("/api/serve/mainPage/getRecyclePics.action")  
	@ResponseBody 
	public Map<String, Object> getServices(HttpServletRequest request, String kind) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = serveMainPageService.getRecyclePics(basePath, kind);
		modelMap.put("success", true);
		
		return modelMap;
	}
	
	
	/**
	 * 获取所有服务信息
	 */
	@RequestMapping("/api/serve/mainPage/getServices.action")  
	@ResponseBody 
	public Map<String, Object> getServices(HttpServletRequest request, String kind, String type) {
		String basePath = BaseUtil.getBasePath(request);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("configs", serveMainPageService.getServices(basePath, kind,type));
		modelMap.put("success", true);
		return modelMap;
	}
	
}

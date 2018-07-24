package com.uas.api.serve.controller;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.api.serve.service.WisdomParkAgreementService;


@Controller
public class WisdomParkAgreementController {
	
	@Autowired
	private WisdomParkAgreementService wisdomParkAgreementService;

	/**
	 * 获取服务协议内容
	 */
	@RequestMapping("/api/serve/mainPage/getAgreementContent.action")  
	@ResponseBody 
	public Map<String, Object> getAgreementContent(HttpServletRequest request, String type) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("content",wisdomParkAgreementService.getAgreementContent(request, type));
		modelMap.put("success", true);
		return modelMap;
	}
}

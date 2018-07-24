package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.service.oa.SignService;

@Controller
public class SignController {
	@Autowired
	private SignService signService;

	@RequestMapping(value = "/oa/getMySign.action")
	@ResponseBody
	public Map<String, Object> getMySign(String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("sign", signService.getMySign(SystemSession.getUser()));
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/oa/signin.action")
	@ResponseBody
	public Map<String, Object> signin(String caller, String reason) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signService.signin(SystemSession.getUser(),reason);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping(value = "/oa/signout.action")
	@ResponseBody
	public Map<String, Object> signout(String caller, String reason) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		signService.signout(SystemSession.getUser(),reason);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.StringUtil;
import com.uas.erp.service.common.InitHandService;

@Controller
public class InitHandController {
	@Autowired
	private InitHandService initHandService;

	/**
	 * 应付确认开账
	 * */
	@RequestMapping(value = "/common/GL/refreshAP.action")
	@ResponseBody
	public Map<String, Object> refreshAP(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initHandService.refreshAP();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收确认开账
	 * */
	@RequestMapping(value = "/common/GL/refreshAR.action")
	@ResponseBody
	public Map<String, Object> refreshAR(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		initHandService.refreshAR();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账开账
	 * */
	@RequestMapping(value = "/common/GL/refreshLedger.action")
	@ResponseBody
	public Map<String, Object> refreshLedger(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String res = initHandService.refreshLedger();
		if (StringUtil.hasText(res))
			modelMap.put("error", true);
		modelMap.put("result", res);
		return modelMap;
	}
	
	/**
	 * 刷新视图JPROCESSVIEW
	 * */
	@RequestMapping(value = "/common/GL/refreshJprocessview.action")
	@ResponseBody
	public Map<String, Object> refreshJprocessview(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String res = initHandService.refreshJprocessview();
		if (StringUtil.hasText(res))
			modelMap.put("error", true);
		modelMap.put("result", res);
		return modelMap;
	}
	
	/**
	 * 刷新视图OA_MESSAGEHISTORY_VIEW
	 * */
	@RequestMapping(value = "/common/GL/refreshOamessagehistoryview.action")
	@ResponseBody
	public Map<String, Object> refreshOamessagehistoryview(HttpSession session) throws Exception {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String res = initHandService.refreshOamessagehistoryview();
		if (StringUtil.hasText(res))
			modelMap.put("error", true);
		modelMap.put("result", res);
		return modelMap;
	}
	
	
	
	
	
	
}

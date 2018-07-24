package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.fa.MQueryService;


@Controller("mQueryController")
public class MQueryController extends BaseController {
	@Autowired
	private MQueryService mQueryService;
	/**
	 * 查询
	 */
	@RequestMapping("/fa/fix/mQueryController/getMQuery.action")  
	@ResponseBody 
	public Map<String, Object> getCmQuery(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", mQueryService.getMQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 查银行存取款
	 */
	@RequestMapping("/fa/gs/mQueryController/getARDateQuery.action")  
	@ResponseBody 
	public Map<String, Object> getARDateQuery(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", mQueryService.getARDateQuery(condition));
		modelMap.put("success", true);
		return modelMap;
	}
}


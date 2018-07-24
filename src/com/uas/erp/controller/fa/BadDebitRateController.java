package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.BadDebitRateService;

@Controller
public class BadDebitRateController {
	@Autowired
	private BadDebitRateService badDebitRateService;

	@RequestMapping("/fa/ars/updateBadDebitRate.action")
	@ResponseBody
	public Map<String, Object> updateBadDebitRate(HttpSession session,
			String caller, String param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		badDebitRateService.updateBadDebitRateById(null, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

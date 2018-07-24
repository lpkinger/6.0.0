package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.ExchangeGlService;

@Controller
public class ExchangeGlController {

	@Autowired
	private ExchangeGlService exchangeGlService;

	@RequestMapping("/fa/gla/exchangegl.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String yearmonth,
			String ca_code, Boolean account, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",
				exchangeGlService.exchange(yearmonth, ca_code, account, data));
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CalCreditService;

@Controller
public class CalCreditController {
	@Autowired
	private CalCreditService CalCreditService;

	/**
	 * 刷新所有客户的额度
	 */
	@RequestMapping("fa/fp/CalCreditRefreshCredit.action")
	@ResponseBody
	public Map<String, Object> CalCreditRefreshCredit(String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		CalCreditService.RefreshCredit(param);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.RepaymentService;

@Controller
public class RepaymentController {

	@Autowired
	private RepaymentService repaymentService;

	/**
	 * 确认还款
	 */
	@RequestMapping("/fs/buss/confirmRepayment.action")
	@ResponseBody
	public Map<String, Object> ConfirmRepayment(String aacode, String aakind, Double thisamount, Double backcustamount, String backdate) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		repaymentService.ConfirmRepayment(aacode, aakind, thisamount, backcustamount, backdate);
		modelMap.put("success", true);
		return modelMap;
	}

}

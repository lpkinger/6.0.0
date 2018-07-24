package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.MonthCarryOverService;

@Controller
public class MonthCarryoverController extends BaseController {
	@Autowired
	private MonthCarryOverService monthCarryOverService;

	/**
	 * 月底结账
	 */
	@RequestMapping("/fa/fix/confirmMonthCarryover.action")
	@ResponseBody
	public Map<String, Object> confirmMonthCarryover(HttpSession session,
			Integer date) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthCarryOverService.carryover(date);
		modelMap.put("success", true);
		return modelMap;
	}
}

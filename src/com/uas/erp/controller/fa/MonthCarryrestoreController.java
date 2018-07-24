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
public class MonthCarryrestoreController extends BaseController {

	@Autowired
	private MonthCarryOverService monthCarryOverService;

	/**
	 * 反结转
	 */
	@RequestMapping("/fa/fix/confirmMonthCarryrestore.action")
	@ResponseBody
	public Map<String, Object> confirmMonthCarryrestore(HttpSession session,
			String caller, Integer date) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthCarryOverService.rescarryover(date);
		modelMap.put("success", true);
		return modelMap;
	}
}

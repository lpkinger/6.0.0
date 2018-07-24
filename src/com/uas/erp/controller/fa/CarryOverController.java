package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CarryOverService;

@Controller
public class CarryOverController {

	@Autowired
	private CarryOverService carryOverService;

	/**
	 * 本月完工结转
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/fa/gla/makecomplete.action")
	@ResponseBody
	public Map<String, Object> makeComplete(HttpSession session, Boolean account) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", carryOverService.makeMonthComplete(account));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 研发费用结转
	 * 
	 * @param session
	 * @return
	 */
	@RequestMapping("/fa/gla/researchfee.action")
	@ResponseBody
	public Map<String, Object> researchFeeComplete(HttpSession session, Boolean account) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", carryOverService.researchCost(account));
		modelMap.put("success", true);
		return modelMap;
	}
}

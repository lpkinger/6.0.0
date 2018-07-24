package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.CarryGlService;

@Controller
public class CarryGlController {

	@Autowired
	private CarryGlService carryGlService;

	/**
	 * 结转损益
	 * 
	 * @param session
	 * @param yearmonth
	 * @param ca_code
	 * @param account
	 * @return
	 */
	@RequestMapping("/fa/gla/carrygl.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String yearmonth,
			String ca_code, Boolean account) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", carryGlService.create(yearmonth, ca_code, account));
		modelMap.put("success", true);
		return modelMap;
	}
}

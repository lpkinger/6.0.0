package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.ChargeBackService;
@Controller
public class ChargeBackController{
	@Autowired
	private ChargeBackService chargeBackService;
	
	/**借机扣款统计
	 */
	@RequestMapping("/scm/sale/calChargeBack.action")  
	@ResponseBody 
	public Map<String, Object> reportAccount(HttpSession session, Integer date) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		chargeBackService.calChargeBack(date);
		modelMap.put("success", true);
		return modelMap;
	}
}

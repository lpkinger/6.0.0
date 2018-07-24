package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MakeMonthService;

@Controller
public class MakeMonth {
	
	@Autowired
	private MakeMonthService makeMonthService;
	
	/**
	 * RefreshProdMonthNew
	 * */
	@RequestMapping("/pm/make/MakeMonth.action")
	@ResponseBody
	public Map<String, Object> RefreshProdMonthNew(String caller, String currentMonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeMonthService.RefreshMakeMonthNew(currentMonth, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	

}

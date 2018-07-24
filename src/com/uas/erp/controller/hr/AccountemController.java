package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.AccountemService;

@Controller
public class AccountemController {

	@Autowired
	private AccountemService accountService;

	/**
	 * 更改
	 */
	@RequestMapping("/hr/emplmana/updateemAccount.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountService.updateAccountById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 复制settings
	 * */
	@RequestMapping("/hr/emplmana/copyRelativeSettings.action")
	@ResponseBody
	public Map<String, Object> copyRelativeSettings(String caller,
			String toobjects, int fromemid) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		accountService.copyRelativeSettings(toobjects, fromemid, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

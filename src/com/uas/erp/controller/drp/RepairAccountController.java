package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.RepairAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RepairAccountController {

	@Autowired
	private RepairAccountService repairAccountService;

	/**
	 * 维修结算单开票
	 */
	@RequestMapping("/drp/aftersale/makeBill.action")
	@ResponseBody
	public Map<String, Object> save(String ra_id, String formStore,
			String param, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairAccountService.makeBill(ra_id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

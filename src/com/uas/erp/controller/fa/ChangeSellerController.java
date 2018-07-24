package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.ChangeSellerService;

@Controller("changeSellerController")
public class ChangeSellerController extends BaseController {
	@Autowired
	private ChangeSellerService changeSellerService;

	/**
	 * 业务员转移
	 * 
	 * @param _businessLimit
	 *            业务控制
	 */
	@RequestMapping("/fa/ars/ChangeSellerController/changeSeller.action")
	@ResponseBody
	public Map<String, Object> changeSeller(HttpSession session,
			String condition, Boolean _businessLimit,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		changeSellerService.changeSeller(condition, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

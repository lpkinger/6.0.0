package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.AutoPrePaidService;

@Controller
public class AutoPrePaidController extends BaseController {
	@Autowired
	private AutoPrePaidService autoPrePaidService;

	/**
	 * 生产摊销单据
	 */
	@RequestMapping("/fa/gla/confirmAutoPrePaid.action")
	@ResponseBody
	public Map<String, Object> confirmAutoPrePaid(HttpSession session,
			Integer date) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		autoPrePaidService.autoPrePaid(date);
		modelMap.put("success", true);
		return modelMap;
	}
}

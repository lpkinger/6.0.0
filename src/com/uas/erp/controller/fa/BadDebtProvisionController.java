package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.BadDebitRateService;

@Controller
public class BadDebtProvisionController extends BaseController {
	@Autowired
	private BadDebitRateService badDebitRateService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/fa/ars/confirmBadDebtProvision.action")
	@ResponseBody
	public Map<String, Object> confirmBadDebtProvision(HttpSession session,
			String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		badDebitRateService.confirmBadDebtProvision(data, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

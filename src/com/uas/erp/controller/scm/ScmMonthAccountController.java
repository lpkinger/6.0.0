package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.MonthAccountService;
import com.uas.erp.service.scm.ScmMonthAccountService;

/**
 * 期末处理
 */
@Controller
public class ScmMonthAccountController {

	@Autowired
	private ScmMonthAccountService scmMonthAccountService;

	@Autowired
	private MonthAccountService monthAccountService;

	/**
	 * 期末总额
	 */
	@RequestMapping("/scm/reserve/monthAccount.action")
	@ResponseBody
	public Map<String, Object> monthAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject js = JSONObject.fromObject(condition);
		modelMap.put("data", scmMonthAccountService.getScmAccount(js.getBoolean("chkun")));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 期末总额
	 */
	@RequestMapping("/scm/reserve/monthAccountDetail.action")
	@ResponseBody
	public Map<String, Object> monthAccountDetail(HttpSession session, int yearmonth, String catecode, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", scmMonthAccountService.getScmAccountDetail(yearmonth, catecode, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 全部存货科目差异
	 */
	@RequestMapping("/scm/reserve/getDifferAll.action")
	@ResponseBody
	public Map<String, Object> getDifferAll(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", scmMonthAccountService.getDifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

}

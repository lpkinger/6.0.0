package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.MonthAccountService;

/**
 * 期末处理
 */
@Controller
public class MonthAccountController {

	@Autowired
	private MonthAccountService monthAccountService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 期末结转
	 */
	@RequestMapping("/fa/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccount(HttpSession session, Integer yearmonth, String module, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.startAccount(yearmonth, module, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 期末反结转
	 */
	@RequestMapping("/fa/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccount(HttpSession session, Integer yearmonth, String module, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.overAccount(yearmonth, module, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收期末结转
	 */
	@RequestMapping("/fa/ars/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		monthAccountService.startAccount(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收期末反结转
	 */
	@RequestMapping("/fa/ars/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		monthAccountService.overAccount(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付期末结转
	 */
	@RequestMapping("/fa/arp/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccountAP(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		monthAccountService.startAccountAP(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付期末反结转
	 */
	@RequestMapping("/fa/arp/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccountAP(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		monthAccountService.overAccountAP(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账期末结转
	 */
	@RequestMapping("/fa/gla/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccountGL(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthGL();
		monthAccountService.startAccountGL(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 总账期末反结转
	 */
	@RequestMapping("/fa/gla/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccountGL(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthGL();
		monthAccountService.overAccountGL(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行票据期末结转
	 */
	@RequestMapping("/fa/gs/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccount(HttpSession session, String caller, Integer param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.startAccount(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行票据期末反结转
	 */
	@RequestMapping("/fa/gs/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccount(HttpSession session, String caller, Integer param) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.overAccount(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收期末总额
	 */
	@RequestMapping("/fa/ars/monthAccount.action")
	@ResponseBody
	public Map<String, Object> arAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountService.getArAccount(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付期末总额
	 */
	@RequestMapping("/fa/arp/monthAccount.action")
	@ResponseBody
	public Map<String, Object> apAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountService.getApAccount(condition));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 固定资产、累计折旧期末对账
	 */
	@RequestMapping("/fa/fix/monthAccount.action")
	@ResponseBody
	public Map<String, Object> fixAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject js = JSONObject.fromObject(condition);
		boolean chkun = js.getBoolean("chkun");
		if (chkun)
			monthAccountService.preWriteVoucher();
		List<Map<String, Object>> data = monthAccountService.getFixAccount(chkun);
		data.addAll(monthAccountService.getDepreAccount(chkun));
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 银行、票据期末对账
	 */
	@RequestMapping("/fa/gs/monthAccount.action")
	@ResponseBody
	public Map<String, Object> gsAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONObject js = JSONObject.fromObject(condition);
		boolean chkun = js.getBoolean("chkun");
		if (chkun)
			monthAccountService.preWriteVoucher();
		monthAccountService.refreshEndData("CB");
		List<Map<String, Object>> data = monthAccountService.getBankAccount(chkun);
		data.addAll(monthAccountService.getBillArAccount(chkun));
		data.addAll(monthAccountService.getBillApAccount(chkun));
		modelMap.put("data", data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本
	 */
	@RequestMapping("/cost/cost/monthAccount.action")
	@ResponseBody
	public Map<String, Object> costAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 存货
	 */
	@RequestMapping("/cost/stock/monthAccount.action")
	@ResponseBody
	public Map<String, Object> stockAccount(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 项目成本期末结转
	 */
	@RequestMapping("/plm/cost/startAccount.action")
	@ResponseBody
	public Map<String, Object> startAccountPLM(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthPLM();
		monthAccountService.startAccountPLM(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 项目成本期末反结转
	 */
	@RequestMapping("/plm/cost/overAccount.action")
	@ResponseBody
	public Map<String, Object> overAccountPLM(HttpSession session, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int yearmonth = autoDepreciationService.getCurrentYearmonthPLM();
		monthAccountService.overAccountPLM(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取分摊系数
	 */
	@RequestMapping("/fa/gla/getShareRate.action")
	@ResponseBody
	public Map<String, Object> getShareRate(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.getShareRate(param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 分摊凭证生成
	 */
	@RequestMapping("/fa/gla/createShareVoucher.action")
	@ResponseBody
	public Map<String, Object> createShareVoucher(Integer param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		monthAccountService.createShareVoucher(param);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.MonthAccountDifferService;

/**
 * 期末处理
 */
@Controller
public class MonthAccountDifferController {

	@Autowired
	private MonthAccountDifferService monthAccountDifferService;

	/**
	 * 应收单个客户差异
	 */
	@RequestMapping("/fa/ars/getARDifferByCust.action")
	@ResponseBody
	public Map<String, Object> getAmDetailByCust(HttpSession session, int yearmonth, String custcode, String currency, String catecode,
			boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getARDifferByCust(yearmonth, custcode, currency, catecode, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应收全部客户差异
	 */
	@RequestMapping("/fa/ars/getARDifferAll.action")
	@ResponseBody
	public Map<String, Object> arAccount(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getARDifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付单个客户差异
	 */
	@RequestMapping("/fa/arp/getAPDifferByVend.action")
	@ResponseBody
	public Map<String, Object> getAmDetailByVend(HttpSession session, int yearmonth, String vendcode, String currency, String catecode,
			boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getAPDifferByVend(yearmonth, vendcode, currency, catecode, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 应付全部客户差异
	 */
	@RequestMapping("/fa/arp/getAPDifferAll.action")
	@ResponseBody
	public Map<String, Object> apAccount(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getAPDifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 票据单个差异
	 */
	@RequestMapping("/fa/gs/getDifferByCode.action")
	@ResponseBody
	public Map<String, Object> getGSDifferByCode(HttpSession session, int yearmonth, String code, String currency, String type,
			boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getGSDifferByCode(yearmonth, code, currency, type, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 票据全部差异
	 */
	@RequestMapping("/fa/gs/getDifferAll.action")
	@ResponseBody
	public Map<String, Object> getGSDifferAll(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getGSDifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本单个差异
	 */
	@RequestMapping("/co/cost/getDifferByCode.action")
	@ResponseBody
	public Map<String, Object> getCODifferByCode(HttpSession session, int yearmonth, String type, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getCODifferByCode(yearmonth, type, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 成本全部差异
	 */
	@RequestMapping("/co/cost/getDifferAll.action")
	@ResponseBody
	public Map<String, Object> getCODifferAll(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getCODifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 固定资产单个差异
	 */
	@RequestMapping("/fa/fix/getDifferByCateCode.action")
	@ResponseBody
	public Map<String, Object> getASDifferByCateCode(HttpSession session, int yearmonth, String catecode, String type,boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getASDifferByCateCode(yearmonth, catecode, type, chkun));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 固定资产全部差异
	 */
	@RequestMapping("/fa/fix/getDifferAll.action")
	@ResponseBody
	public Map<String, Object> getASDifferAll(HttpSession session, int yearmonth, boolean chkun) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", monthAccountDifferService.getASDifferAll(yearmonth, chkun));
		modelMap.put("success", true);
		return modelMap;
	}
}

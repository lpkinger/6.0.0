package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fa.AutoDepreciationService;
import com.uas.erp.service.fa.ProduceBudgetBillService;

@Controller
public class ProduceBudgetBillController {

	@Autowired
	private ProduceBudgetBillService ProduceBudgetBillService;
	@Autowired
	private AutoDepreciationService autoDepreciationService;

	/**
	 * 生成收款预算
	 */
	@RequestMapping("/fa/fp/ProduceBudgetBill.action")
	@ResponseBody
	public Map<String, Object> ProduceBudgetBill(int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		int yearmonth = autoDepreciationService.getCurrentYearmonthAR();
		ProduceBudgetBillService.ProduceBudgetBill(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 付款预算
	 */
	@RequestMapping("/fa/fp/ProduceFKBudgetBill.action")
	@ResponseBody
	public Map<String, Object> ProduceFKBudgetBill(int yearmonth) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
//		int yearmonth = autoDepreciationService.getCurrentYearmonthAP();
		ProduceBudgetBillService.ProduceFKBudgetBill(yearmonth);
		modelMap.put("success", true);
		return modelMap;
	}
}

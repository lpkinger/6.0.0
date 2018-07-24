package com.uas.erp.controller.fa;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;

import com.uas.erp.service.fa.BudgetService;

@Controller("budgetController")
public class BudgetController extends BaseController {
	@Autowired
	private BudgetService budgetService;

	/**
	 * 保存
	 */
	@RequestMapping("/fa/bg/saveBudget.action")
	@ResponseBody
	public Map<String, Object> save(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		budgetService.saveBudget(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/fa/bg/deleteBudget.action")
	@ResponseBody
	public Map<String, Object> deleteAPBill(HttpSession session, int id,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		budgetService.deleteBudget(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/fa/bg/updateBudget.action")
	@ResponseBody
	public Map<String, Object> update(HttpSession session, String formStore,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		budgetService.updateBudgetById(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/fa/bg/printBudget.action")
	@ResponseBody
	public Map<String, Object> printAPBill(HttpSession session, int id,
			String caller, String reportName, String condition) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = budgetService.printBudget(caller, id, reportName,
				condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

}

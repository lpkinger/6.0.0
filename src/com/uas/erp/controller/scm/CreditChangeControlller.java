package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.CreditChangeService;

@Controller
public class CreditChangeControlller extends BaseController {
	@Autowired
	private CreditChangeService creditChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/saveCreditChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditChangeService.saveCreditChange(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/sale/updateCreditChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditChangeService.updateCreditChangeById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核客户信用额度变更单
	 */
	@RequestMapping("/scm/sale/auditCreditChange.action")
	@ResponseBody
	public Map<String, Object> auditCreditChange(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditChangeService.auditCreditChange(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核供应航信用额度变更单
	 */
	@RequestMapping("/scm/purchase/auditCreditChange.action")
	@ResponseBody
	public Map<String, Object> auditVendCreditChange(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditChangeService.auditVendCreditChange(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核客户信用额度
	 */
	@RequestMapping("/scm/sale/auditCustomerCredit.action")
	@ResponseBody
	public Map<String, Object> auditCustomerCredit(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditChangeService.auditCustomerCredit(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.PreCustomerService;

@Controller
public class PreCustomerControlller extends BaseController {
	@Autowired
	private PreCustomerService preCustomerService;
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/savePreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.savePreCustomer(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deletePreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> deletePreCustomer(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.deletePreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/updatePreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.updatePreCustomerById(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printPreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> printPurchase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.printPreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitPreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.submitPreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitPreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitPurchase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.resSubmitPreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditPreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> auditPurchase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.auditPreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditPreCustomer.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchase(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preCustomerService.resAuditPreCustomer(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转客户
	 */
	@RequestMapping("/scm/sale/turnCustomer.action")  
	@ResponseBody 
	public Map<String, Object> turnCustomer(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int cuid = preCustomerService.turnCustomer(id);
		modelMap.put("id", cuid);
		modelMap.put("success", true);
		return modelMap;
	}
}

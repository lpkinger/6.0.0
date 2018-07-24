package com.uas.erp.controller.fs;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.fs.CreditTargetsService;


@Controller
public class CreditTargetsController {
	
	@Autowired
	private CreditTargetsService creditTargetsService;
	
	/**
	 * 获取计算公式项
	 */
	@RequestMapping("/fs/credit/getColItems.action")  
	@ResponseBody 
	public Map<String, Object> getColItems() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data",creditTargetsService.getColItems());
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 保存
	 */
	@RequestMapping("/fs/credit/saveCreditTargets.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.saveCreditTargets(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/fs/credit/updateCreditTargets.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String caller ) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.updateCreditTargets(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/fs/credit/deleteCreditTargets.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.deleteCreditTargets(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/fs/credit/submitCreditTargets.action")
	@ResponseBody
	public Map<String, Object> submitcreditTargets(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.submitCreditTargets(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/fs/credit/resSubmitCreditTargets.action")
	@ResponseBody
	public Map<String, Object> resSubmitcreditTargets(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.resSubmitCreditTargets(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/fs/credit/auditCreditTargets.action")  
	@ResponseBody 
	public Map<String, Object> auditcreditTargets(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.auditCreditTargets(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/fs/credit/resAuditCreditTargets.action")  
	@ResponseBody 
	public Map<String, Object> resAuditPurchase(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.resAuditCreditTargets(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 保存项目值设置
	 */
	@RequestMapping("/fs/credit/saveItemsValue.action")  
	@ResponseBody 
	public Map<String, Object> saveItemsValue(String datas) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.saveItemsValue(datas);
		modelMap.put("success", true);
		return modelMap;
	}	
	
	/**
	 * 删除项目值设置
	 */
	@RequestMapping("/fs/credit/deleteItemsValue.action")  
	@ResponseBody 
	public Map<String, Object> deleteItemsValue(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.deleteItemsValue(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 测试SQL
	 */
	@RequestMapping("/fs/credit/testSQL.action")  
	@ResponseBody 
	public Map<String, Object> testSQL(String sql) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		creditTargetsService.testSQL(sql);
		modelMap.put("success", true);
		return modelMap;
	}
}

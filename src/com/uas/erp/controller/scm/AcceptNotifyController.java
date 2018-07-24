package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.AcceptNotifyService;

@Controller
public class AcceptNotifyController extends BaseController {
	@Autowired
	private AcceptNotifyService acceptNotifyService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.saveAcceptNotify(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> deleteAcceptNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.deleteAcceptNotify(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.updateAcceptNotifyById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> printAcceptNotify(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = acceptNotifyService.printAcceptNotify(id, caller, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> submitAcceptNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.submitAcceptNotify(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitAcceptNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.resSubmitAcceptNotify(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> auditAcceptNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.auditAcceptNotify(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditAcceptNotify.action")  
	@ResponseBody 
	public Map<String, Object> resAuditAcceptNotify(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		acceptNotifyService.resAuditAcceptNotify(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转收料
	 */
	@RequestMapping("/scm/purchase/turnVerifyApply.action")  
	@ResponseBody 
	public Map<String, Object> turnVerifyApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", acceptNotifyService.turnVerifyApply(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转采购验收单
	 */
	@RequestMapping("/scm/purchase/turnProdio.action")  
	@ResponseBody 
	public Map<String, Object> turnProdio(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", acceptNotifyService.turnProdio(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}	
	@RequestMapping("/scm/purchase/saveAcceptNotifyQty.action")  
	@ResponseBody 
	public Map<String, Object> saveAcceptNotifyQty(String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    acceptNotifyService.saveAcceptNotifyQty(data);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/scm/purchase/backAll.action")  
	@ResponseBody 
	public Map<String, Object> backAll(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    acceptNotifyService.backAll(id);
		modelMap.put("success", true);
		return modelMap;
	}
}

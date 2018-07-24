package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BuyerChangeService;

@Controller
public class BuyerChangeController {
	@Autowired
	private BuyerChangeService buyerChangeService;
	
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.saveBuyerChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更改
	 */
	@RequestMapping("/scm/purchase/updateBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore,String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.updateBuyerChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> submitBuyerChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.submitBuyerChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBuyerChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.resSubmitBuyerChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> auditBuyerChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.auditBuyerChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteBuyerChange.action")  
	@ResponseBody 
	public Map<String, Object> deleteBuyerChange(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		buyerChangeService.deleteBuyerChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}

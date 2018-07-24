package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VerifyApplyChangeService;

@Controller
public class VerifyApplyChangeController extends BaseController {
	@Autowired
	private VerifyApplyChangeService verifyApplyChangeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.saveVerifyApplyChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> deletepurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.deleteVerifyApplyChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.updateVerifyApplyChangeById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> submitpurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.submitVerifyApplyChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitpurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.resSubmitVerifyApplyChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVerifyApplyChange.action")  
	@ResponseBody 
	public Map<String, Object> auditpurchaseChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		verifyApplyChangeService.auditVerifyApplyChange( id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

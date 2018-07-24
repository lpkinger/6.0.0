package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BigLineApplyService;

@Controller
public class BigLineApplyController {
	@Autowired
	private BigLineApplyService bigLineApplyService;
//	规范  小写
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/saveBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.saveBigLineApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteBigLineApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.deleteBigLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/updateBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.updateBigLineApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> submitBigLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.submitBigLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBigLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.resSubmitBigLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> auditBigLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.auditBigLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditBigLineApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditBigLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bigLineApplyService.resAuditBigLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

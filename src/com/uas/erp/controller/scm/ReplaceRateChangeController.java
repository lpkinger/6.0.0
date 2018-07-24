package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ReplaceRateChangeService;

@Controller
public class ReplaceRateChangeController extends BaseController {
	@Autowired
	private ReplaceRateChangeService replaceRateChangeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.saveReplaceRateChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> deleteReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.deleteReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.updateReplaceRateChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> printReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.printReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> submitReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.submitReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.resSubmitReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> auditReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.auditReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditReplaceRateChange.action")  
	@ResponseBody 
	public Map<String, Object> resAuditReplaceRateChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		replaceRateChangeService.resAuditReplaceRateChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

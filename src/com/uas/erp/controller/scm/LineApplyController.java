package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.LineApplyService;

@Controller
public class LineApplyController {
	@Autowired
	private LineApplyService lineApplyService;
//	规范  小写
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/saveLineApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.saveLineApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteLineApply.action")  
	@ResponseBody 
	public Map<String, Object> deleteLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.deleteLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/scm/sale/updateLineApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.updateLineApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitLineApply.action")  
	@ResponseBody 
	public Map<String, Object> submitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.submitLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitLineApply.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.resSubmitLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditLineApply.action")  
	@ResponseBody 
	public Map<String, Object> auditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.auditLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditLineApply.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lineApplyService.resAuditLineApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

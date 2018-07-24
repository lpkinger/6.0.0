package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.TiChengService;

@Controller
public class TiChengController {
	@Autowired
	private TiChengService tiChengService;

	/**
	 * 保存
	 */
	@RequestMapping("/oa/fee/saveTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.saveTiCheng(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/oa/fee/deleteTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> deleteAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.deleteTiCheng(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更新
	 */
	@RequestMapping("/oa/fee/updateTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.updateTiChengById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/oa/fee/submitTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> submitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.submitTiCheng(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/oa/fee/resSubmitTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.resSubmitTiCheng(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/oa/fee/auditTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> auditAssistRequire(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.auditTiCheng(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/oa/fee/resAuditTiCheng.action")  
	@ResponseBody 
	public Map<String, Object> resAuditAssistRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		tiChengService.resAuditTiCheng(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

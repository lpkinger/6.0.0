package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.AskLeaveService;

@Controller
public class AskLeaveController {
	@Autowired
	private AskLeaveService askLeaveService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/oa/attendance/saveAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.saveAskLeave(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/oa/attendance/updateAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.updateAskLeave(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/oa/attendance/deleteAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.deleteAskLeave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核AskLeave
	 */
	@RequestMapping("/oa/attendance/auditAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> audit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.auditAskLeave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/oa/attendance/resAuditAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.resAuditAskLeave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/oa/attendance/submitAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.submitAskLeave(id,  caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/oa/attendance/resSubmitAskLeave.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		askLeaveService.resSubmitAskLeave(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

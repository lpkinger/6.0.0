package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.RequireService;



@Controller
public class RequireController {
	@Autowired
	private RequireService requireService;
	/**
	 * 保存
	 */
	@RequestMapping("/plm/request/saveRequire.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.saveRequire(formStore,caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/plm/request/updateRequire.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.updateRequireById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/plm/request/deleteRequire.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.deleteRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/plm/request/submitRequire.action")
	@ResponseBody
	public Map<String, Object> submitRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.submitRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/plm/request/resSubmitRequire.action")
	@ResponseBody
	public Map<String, Object> resSubmitRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.resSubmitRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/plm/request/auditRequire.action")  
	@ResponseBody 
	public Map<String, Object> auditRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.auditRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/plm/request/resAuditRequire.action")
	@ResponseBody
	public Map<String, Object> resAuditRequire(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		requireService.resAuditRequire(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  转立项
	 */
	@RequestMapping("/plm/request/turnProject.action")  
	@ResponseBody 
	public Map<String, Object> turnProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", requireService.turnProject(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *  转预立项
	 */
	@RequestMapping("/plm/request/turnPrepProject.action")  
	@ResponseBody 
	public Map<String, Object> turnPrepProject(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", requireService.turnPrepProject(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
}

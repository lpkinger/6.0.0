package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.RoleService;



@Controller
public class RoleController {
	@Autowired
	private RoleService roleService;
	
	/*
	 * 保存角色
	 */
	@RequestMapping(value = "/hr/employee/saveRole.action")
	@ResponseBody
	public Map<String,Object> saveRole(String caller,String formStore,String param) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		roleService.saveRole(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/*
	 * 更新角色
	 */
	@RequestMapping(value = "/hr/employee/updateRole.action")
	@ResponseBody
	public Map<String,Object> updateRole(String caller,String formStore,String param) {
		Map<String,Object> modelMap = new HashMap<String,Object>();
		roleService.updateRole(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/hr/employee/deleteRole.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.deleteRole(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核
	 */
	@RequestMapping("/hr/employee/auditRole.action")
	@ResponseBody
	public Map<String, Object> audit(String caller, int id) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.auditRole(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核
	 */
	@RequestMapping("/hr/employee/resAuditRole.action")
	@ResponseBody
	public Map<String, Object> resAudit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.resAuditRole(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/hr/employee/submitRole.action")
	@ResponseBody
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.submitRole(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/hr/employee/resSubmitRole.action")
	@ResponseBody
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.resSubmitRole(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用
	 */
	@RequestMapping("/hr/employee/bannedRole.action")
	@ResponseBody
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.bannedRole(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反禁用
	 */
	@RequestMapping("/hr/employee/resBannedRole.action")
	@ResponseBody
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		roleService.resBannedRole(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
}

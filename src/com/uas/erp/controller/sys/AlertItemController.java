package com.uas.erp.controller.sys;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.sys.AlertItemService;

@Controller
public class AlertItemController extends BaseController {
	@Autowired
	private AlertItemService alertItemService;
	
	@RequestMapping("sys/alert/saveAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,String formStore,String params1,String params2) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.save(caller,formStore,params1,params2);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/updateAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller,String formStore,String params1,String params2) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.update(caller, formStore, params1, params2);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/submitAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.submit(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resSubmitAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.resSubmit(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/auditAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.audit(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resAuditAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.resAudit(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/deleteAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.delete(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resBannedAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.resBanned(id, caller);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/bannedAlertItem.action")  
	@ResponseBody 
	public Map<String, Object> banned(HttpSession session, int id, String caller) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertItemService.banned(id, caller);
		resMap.put("success", true);
		return resMap;
	}
}

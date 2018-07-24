package com.uas.erp.controller.sys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.sys.AlertInstanceService;

import net.sf.json.JSONObject;

@Controller
public class AlertInstanceController {
	
	@Autowired
	private AlertInstanceService alertInstanceService;
	
	@RequestMapping("sys/alert/saveAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String caller, String baseFormStore, String paramFormStore, String assignGridRecord) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.save(caller, baseFormStore, paramFormStore, assignGridRecord);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/updateAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String caller, String baseFormStore, String paramFormStore, String assignGridRecord) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.update(caller, baseFormStore, paramFormStore, assignGridRecord);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/submitAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.submit(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resSubmitAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.resSubmit(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/auditAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.audit(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resAuditAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.resAudit(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/deleteAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.delete(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/bannedAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> banned(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.banned(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/resBannedAlertInstance.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(HttpSession session, String caller, int id) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertInstanceService.resBanned(caller, id);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/getParamItems.action")  
	@ResponseBody 
	public Map<String, Object> getParamItems(HttpSession session, String itemId, String instanceId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<JSONObject> paramItems = new ArrayList<JSONObject>();
		if(itemId != null && !itemId.isEmpty()) {
			paramItems = alertInstanceService.getParamItems(itemId, null);
		}else if(instanceId != null && !instanceId.isEmpty()) {
			paramItems = alertInstanceService.getParamItems(null, instanceId);
		}
		resMap.put("success", true);
		resMap.put("data", paramItems);
		return resMap;
	}
	
	@RequestMapping("sys/alert/getAssign.action")  
	@ResponseBody 
	public Map<String, Object> getAssign(HttpSession session, String instanceId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<JSONObject> paramItems = new ArrayList<JSONObject>();
		paramItems = alertInstanceService.getAssign(instanceId);
		resMap.put("success", true);
		resMap.put("data", paramItems);
		return resMap;
	}
	
	@RequestMapping("sys/alert/getOutputParams.action")  
	@ResponseBody 
	public Map<String, Object> getOutputParams(HttpSession session, String itemId) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		List<JSONObject> paramItems = new ArrayList<JSONObject>();
		paramItems = alertInstanceService.getOutputParams(itemId);
		resMap.put("success", true);
		resMap.put("data", paramItems);
		return resMap;
	}
}

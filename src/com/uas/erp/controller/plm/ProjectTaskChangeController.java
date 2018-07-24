package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.ProjectTaskChangeService;
@Controller
public class ProjectTaskChangeController extends BaseController{
@Autowired
private ProjectTaskChangeService projectTaskChangeService;
@RequestMapping("/plm/change/saveProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> saveProjectTaskChange(HttpSession session, String formStore, String param) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.saveProjectTaskChange(formStore, param);
	modelMap.put("success", true);
	return modelMap;
 }
@RequestMapping("/plm/change/deleteProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> deleteProjectTaskChange(HttpSession session, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.deleteProjectTaskChange(id);
	modelMap.put("success", true);
	return modelMap;
}

@RequestMapping("/plm/change/updateProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> updateProjectTaskChange(HttpSession session, String formStore, String param) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.updateProjectTaskChange(formStore, param);
	modelMap.put("success", true);
	return modelMap;
}
@RequestMapping("/plm/change/submitProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> submitProjectTaskChange(HttpSession session, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.submitProjectTaskChange(id);
	modelMap.put("success", true);
	return modelMap;
}
/**
 * 反提交
 */
@RequestMapping("/plm/change/resSubmitProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> resSubmitProjectTaskChange(HttpSession session, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.resSubmitProjectTaskChange(id);
	modelMap.put("success", true);
	return modelMap;
}
/**
 * 审核
 */
@RequestMapping("/plm/change/auditProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> auditProjectTaskChange(HttpSession session,int id ) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.auditProjectTaskChange(id);
	modelMap.put("success", true);
	return modelMap;
}
/**
 * 反审核
 */
@RequestMapping("/plm/change/resAuditProjectTaskChange.action")  
@ResponseBody 
public Map<String, Object> resAuditProjectTaskChange(HttpSession session, int id) {
	Map<String, Object> modelMap = new HashMap<String, Object>();
	projectTaskChangeService.resAuditProjectTaskChange(id);
	modelMap.put("success", true);
	return modelMap;
}
}


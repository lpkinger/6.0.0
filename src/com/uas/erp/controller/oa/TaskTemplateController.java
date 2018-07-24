package com.uas.erp.controller.oa;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.oa.TaskTemplateService;
@Controller
public class TaskTemplateController {
	@Autowired
	private TaskTemplateService taskTemplateService;
	@RequestMapping("oa/task/saveTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
        taskTemplateService.saveTaskTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("oa/task/deleteTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> delete(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskTemplateService.deleteTaskTemplate(id);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更新
	 */
	@RequestMapping("oa/task/updateTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    taskTemplateService.updateTaskTemplate(formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 * */
	@RequestMapping("/oa/task/bannedTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> bannedTaskTemplate(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskTemplateService.bannedTaskTemplate(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *反禁用
	 * */
	
	@RequestMapping("/oa/task/resBannedTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		taskTemplateService.resBannedTaskTemplate(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 载入任务模板
	 * */
	@RequestMapping("oa/task/loadTaskTemplate.action")  
	@ResponseBody 
	public Map<String, Object> loadTaskTemplate(String caller,int keyValue) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    taskTemplateService.loadTaskTemplate(caller,keyValue);
		modelMap.put("success", true);
		return modelMap;
	}
}

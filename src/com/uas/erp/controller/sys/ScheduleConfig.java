package com.uas.erp.controller.sys;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.sys.ScheduleConfigService;

@Controller
public class ScheduleConfig {
	
	@Autowired
	private ScheduleConfigService scheduleConfigService;

	@RequestMapping("/sys/scheduleConfig/save.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//执行保存
		scheduleConfigService.save(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/sys/scheduleConfig/update.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//执行更新
		scheduleConfigService.update(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("/sys/scheduleConfig/delete.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//执行删除
		scheduleConfigService.deleteDocSetting(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

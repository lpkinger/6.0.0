package com.uas.erp.controller.wisdomPark;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.service.wisdomPark.ActivityCenterService;

@Controller
public class ActivityCenterController {
	
	
	@Autowired
	private ActivityCenterService activityCenterService;
	
	
	//删除活动类型
	@RequestMapping("/wisdomPark/activityCenter/deleteActivityType.action")
	@ResponseBody
	public Map<String, Object> deleteActivityType(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.deleteActivityType(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//保存活动
	@RequestMapping("/wisdomPark/activityCenter/saveActivity.action")
	@ResponseBody
	public Map<String, Object> saveActivity(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.saveActivity(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
		
	}
	
	//更新活动
	@RequestMapping("/wisdomPark/activityCenter/updateActivity.action")
	@ResponseBody
	public Map<String, Object> updateActivity(String caller, String formStore){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.updateActivity(caller, formStore);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//删除活动
	@RequestMapping("/wisdomPark/activityCenter/deleteActivity.action")
	@ResponseBody
	public Map<String, Object> deleteActivity(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.deleteActivity(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//发布活动
	@RequestMapping("/wisdomPark/activityCenter/publishActivity.action")
	@ResponseBody
	public Map<String, Object> publishActivity(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.publishActivity(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//撤销活动
	@RequestMapping("/wisdomPark/activityCenter/cancelActivity.action")
	@ResponseBody
	public Map<String, Object> cancelActivity(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.cancelActivity(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
	//提前结束活动
	@RequestMapping("/wisdomPark/activityCenter/advanceEndActivity.action")
	@ResponseBody
	public Map<String, Object> advanceEndActivity(String caller, int id){	
		Map<String, Object> modelMap = new HashMap<String, Object>();
		activityCenterService.advanceEndActivity(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.service.CanlendarTaskService;

@Controller
public class CanlendarTaskController {
 @Autowired
 private CanlendarTaskService canlendarTaskService;
 @RequestMapping(value="/mobile/getCanlendarTask.action")
 @ResponseBody
 public Map<String,Object> getCanlendarTask(HttpServletRequest req,String condition,String sessionId){
	 Map<String,Object> modelMap=new HashMap<String,Object>();
	 modelMap.put("success",true);
	 modelMap.put("tasks", canlendarTaskService.getCanlendarTask(condition, sessionId));
	 return modelMap;
 }
}

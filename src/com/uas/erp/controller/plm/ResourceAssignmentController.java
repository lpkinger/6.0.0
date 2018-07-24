package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.plm.ResourceAssignmentService;

@Controller
public class ResourceAssignmentController {
	@Autowired
	private ResourceAssignmentService resourceAssignmentService;
	@RequestMapping(value="plm/resourceassignment.action")
	@ResponseBody
	public Map<String, Object> getData(HttpServletResponse resp,String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONArray resources=resourceAssignmentService.getData("Teammember","tm_prjid ="+condition.split("=")[1]);
		JSONArray assignments=resourceAssignmentService.getData("ProjectTask","prjplanid ="+condition.split("=")[1]);
		modelMap.put("resources", resources);
		modelMap.put("assignments", assignments);
		return modelMap;
	}
	
	@RequestMapping(value="task/resource.action")
	@ResponseBody
	public Map<String, Object> getTaskResourceData(HttpServletResponse resp,String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONArray resources=resourceAssignmentService.getTaskResourceData("Teammember","tm_prjid ="+condition.split("=")[1]);
		modelMap.put("resources", resources);
		return modelMap;
	}
	
	@RequestMapping(value="task/assignment.action")
	@ResponseBody
	public Map<String, Object> getTaskAssignment(HttpServletResponse resp,String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(!condition.equals(null)){
			JSONArray assignments=resourceAssignmentService.getTaskAssignmentData("ResourceAssignment","ra_prjid ="+condition.split("=")[1]);
			modelMap.put("assignments", assignments);
		}else{
			JSONArray assignments=resourceAssignmentService.getTaskAssignmentData("ResourceAssignment","");	
			modelMap.put("assignments", assignments);
		}
		return modelMap;
	}
	
	@RequestMapping(value="plm/resourceassignmentx.action")
	@ResponseBody
	public Map<String, Object> getDatax(HttpServletResponse resp,String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		JSONArray resources=resourceAssignmentService.getData("Teammember","tm_prjid ="+condition.split("=")[1]);
		JSONArray assignments=resourceAssignmentService.getData("ResourceAssignment","ra_prjid ="+condition.split("=")[1]);
		modelMap.put("resources", resources);
		modelMap.put("assignments", assignments);
		return modelMap;
	}
	@RequestMapping(value="plm/resource/getResourceData.action")
	@ResponseBody
	public Map<String,Object>getResourceData(HttpServletResponse resp,String id) throws Exception{
		Map<String,Object>modelMap=new HashMap<String,Object>();
		JSONObject resourceData=resourceAssignmentService.getResourceData(id);
		modelMap.put("data", resourceData);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping(value="plm/resource/createResource.action")
	@ResponseBody
	public Map<String,Object>createResource(HttpServletResponse resp,String jsonData) throws Exception{
		Map<String,Object>modelMap=new HashMap<String,Object>();
		resourceAssignmentService.saveAssignment(jsonData);
		modelMap.put("success",true);
		return modelMap;
	}
	@RequestMapping(value="plm/resource/updateResource.action")
	@ResponseBody
	public Map<String,Object>updateResource(HttpServletResponse resp,String jsonData)throws Exception{
     Map<String,Object>modelMap=new HashMap<String,Object>();
		resourceAssignmentService.updateAssignment(jsonData);
		return modelMap;
	} 
	@RequestMapping(value="plm/resource/deleteResource.action")
	@ResponseBody
	public Map<String,Object>deleteResource(HttpServletResponse resp,String jsonData)throws Exception{
	     Map<String,Object>modelMap=new HashMap<String,Object>();
			resourceAssignmentService.deleteAssignment(jsonData);
			return modelMap;
		} 
	
}

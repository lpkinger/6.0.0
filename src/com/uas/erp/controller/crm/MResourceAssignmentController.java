package com.uas.erp.controller.crm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.dao.crm.EmployeeTaskDAO;
import com.uas.erp.dao.crm.SimpleDAO;

@Controller
public class MResourceAssignmentController {
	@Autowired
	private SimpleDAO simpleDao;
	
	@Autowired
	private EmployeeTaskDAO employeeTaskDAO;
	@RequestMapping(value="/market/resource.action")
	@ResponseBody
	public Map<String, Object> MgetTaskResourceData(String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		condition="tm_prjid="+ condition.split("=")[1];
		return simpleDao.dataList(null,condition, "MTeammember","id:tm_id,Name:tm_name,tm_id:tm_id");
	}
	
	@RequestMapping(value="/market/resource/updateResource.action")
	@ResponseBody
	public Map<String,Object> MupdateResource(HttpServletResponse resp,String jsonData)throws Exception{
     Map<String,Object>modelMap=new HashMap<String,Object>();
		return modelMap;
	} 
	@RequestMapping(value="/market/resource/deleteResource.action")
	@ResponseBody
	public Map<String,Object> MdeleteResource(HttpServletResponse resp,String jsonData)throws Exception{
	     Map<String,Object>modelMap=new HashMap<String,Object>();
			return modelMap;
		} 
	@SuppressWarnings({ "unchecked", "unused" })
	@RequestMapping(value="/market/getAssignment.action")
	@ResponseBody
	public Map<String, Object> MgetTaskAssignment(String condition) throws Exception{
		Map<String, Object> modelMap = new HashMap<String, Object>();
		condition="ra_prjid="+ condition.split("=")[1];
		return simpleDao.dataList(null,condition, "MResourceAssignment","id:id,TaskId:ra_taskid,ResourceId:ra_emid,Units:ra_units");
	}
	@RequestMapping(value="/market/updateAssignment.action")
	@ResponseBody
	public String Mupdateassignment(@RequestBody String st,String condition) throws Exception{
		String subkey="ra_prjid";
		String subKeyVal=condition.split("=")[1];
		return simpleDao.dataUpdate(st,subkey,subKeyVal, "MResourceAssignment", false, "id:id,TaskId:ra_taskid,ResourceId:ra_emid,Units:ra_units");
	
	}
	@RequestMapping(value="/market/deleteAssignment.action")
	@ResponseBody
	public String Mdeleteassignment(@RequestBody String st,String condition) throws Exception{
		/*SimpleDAO dsd=new SimpleDAO();
		dsd.dataDelete(st, "MResourceAssignment");*/
		simpleDao.dataDelete(st, "MResourceAssignment");
		return  "{success:true}";	
	}
	@RequestMapping(value="/market/getForTask.action",produces="application/json ; charset=UTF-8")
	@ResponseBody
	public Map<?, ?>  Mget(String startdate,String enddate) throws Exception{
	//public String  Mget(String startdate,String enddate) throws Exception{
		//return "{Id:0,children:[{Id:1,Name:'KastrupAirport',iconCls:'sch-airport',expanded:true,children:[{Id:2,Name:'TerminalA',iconCls:'sch-terminal',expanded:true,children:[{Id:3,Name:'Gates1-5',iconCls:'sch-gates-bundle',expanded:true,children:[{Id:4,Name:'Gate1',leaf:true,iconCls:'sch-gate',Capacity:100},{Id:5,Name:'Gate2',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:6,Name:'Gate3',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:7,Name:'Gate4',leaf:true,iconCls:'sch-gate',Capacity:65},{Id:8,Name:'Gate5',leaf:true,iconCls:'sch-gate',Capacity:75}]},{Id:9,Name:'Gates6-10',iconCls:'sch-gates-bundle',expanded:true,children:[{Id:10,Name:'Gate6',leaf:true,iconCls:'sch-gate',Capacity:77},{Id:11,Name:'Gate7',leaf:true,iconCls:'sch-gate',Capacity:85},{Id:12,Name:'Gate8',leaf:true,iconCls:'sch-gate',Capacity:95},{Id:13,Name:'Gate9',leaf:true,iconCls:'sch-gate',Capacity:55},{Id:14,Name:'Gate10',leaf:true,iconCls:'sch-gate',Capacity:15}]}]}, {Id:15,Name:'TerminalB',iconCls:'sch-terminal',children:[{Id:16,Name:'Gates1-5',iconCls:'sch-gates-bundle',children:[{Id:17,Name:'Gate1',leaf:true,iconCls:'sch-gate',Capacity:15},{Id:18,Name:'Gate2',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:19,Name:'Gate3',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:20,Name:'GateB',leaf:true,iconCls:'sch-gate',Capacity:65},{Id:21,Name:'Gate5',leaf:true,iconCls:'sch-gate',Capacity:70}]},{Id:22,Name:'Gates6-10',iconCls:'sch-gates-bundle',children:[{Id:23,Name:'Gate6',leaf:true,iconCls:'sch-gate',Capacity:80},{Id:24,Name:'Gate7',leaf:true,iconCls:'sch-gate',Capacity:120},{Id:25,Name:'Gate8',leaf:true,iconCls:'sch-gate',Capacity:125},{Id:26,Name:'Gate9',leaf:true,iconCls:'sch-gate',Capacity:100},{Id:27,Name:'Gate10',leaf:true,iconCls:'sch-gate',Capacity:100}]}]}]}]}";
		//return "{\"children\":[{\"Name\":\"人事行政部\",\"Id\":42,\"loaded\":true},{\"Name\":\"实施部\",\"children\":[{\"Name\":\"詹国胜\",\"Id\":3243,\"leaf\":true,\"expanded\":true},{\"Name\":\"陈敬业\",\"Id\":3242,\"leaf\":true,\"expanded\":true},{\"Name\":\"吉伟宁\",\"Id\":3258,\"leaf\":true,\"expanded\":true},{\"Name\":\"孙曲芳\",\"Id\":3257,\"leaf\":true,\"expanded\":true},{\"Name\":\"付家华\",\"Id\":3256,\"leaf\":true,\"expanded\":true},{\"Name\":\"李艳莉\",\"Id\":3255,\"leaf\":true,\"expanded\":true},{\"Name\":\"陈虎\",\"Id\":3254,\"leaf\":true,\"expanded\":true}],\"Id\":45,\"expanded\":true},{\"Name\":\"销售部\",\"children\":[{\"Name\":\"陈小龙\",\"Id\":3253,\"leaf\":true,\"expanded\":true},{\"Name\":\"连冰花\",\"Id\":3252,\"leaf\":true,\"expanded\":true}],\"Id\":46,\"expanded\":true}]}";
		return employeeTaskDAO.getEmployeeData(startdate, enddate);
	}
	@RequestMapping(value="/market/getTasks.action")
	@ResponseBody
	public ArrayList<?>  Mgetevent(String startdate,String enddate) throws Exception{
		//return "{Id:0,children:[{Id:1,Name:'KastrupAirport',iconCls:'sch-airport',expanded:true,children:[{Id:2,Name:'TerminalA',iconCls:'sch-terminal',expanded:true,children:[{Id:3,Name:'Gates1-5',iconCls:'sch-gates-bundle',expanded:true,children:[{Id:4,Name:'Gate1',leaf:true,iconCls:'sch-gate',Capacity:100},{Id:5,Name:'Gate2',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:6,Name:'Gate3',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:7,Name:'Gate4',leaf:true,iconCls:'sch-gate',Capacity:65},{Id:8,Name:'Gate5',leaf:true,iconCls:'sch-gate',Capacity:75}]},{Id:9,Name:'Gates6-10',iconCls:'sch-gates-bundle',expanded:true,children:[{Id:10,Name:'Gate6',leaf:true,iconCls:'sch-gate',Capacity:77},{Id:11,Name:'Gate7',leaf:true,iconCls:'sch-gate',Capacity:85},{Id:12,Name:'Gate8',leaf:true,iconCls:'sch-gate',Capacity:95},{Id:13,Name:'Gate9',leaf:true,iconCls:'sch-gate',Capacity:55},{Id:14,Name:'Gate10',leaf:true,iconCls:'sch-gate',Capacity:15}]}]}, {Id:15,Name:'TerminalB',iconCls:'sch-terminal',children:[{Id:16,Name:'Gates1-5',iconCls:'sch-gates-bundle',children:[{Id:17,Name:'Gate1',leaf:true,iconCls:'sch-gate',Capacity:15},{Id:18,Name:'Gate2',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:19,Name:'Gate3',leaf:true,iconCls:'sch-gate',Capacity:45},{Id:20,Name:'GateB',leaf:true,iconCls:'sch-gate',Capacity:65},{Id:21,Name:'Gate5',leaf:true,iconCls:'sch-gate',Capacity:70}]},{Id:22,Name:'Gates6-10',iconCls:'sch-gates-bundle',children:[{Id:23,Name:'Gate6',leaf:true,iconCls:'sch-gate',Capacity:80},{Id:24,Name:'Gate7',leaf:true,iconCls:'sch-gate',Capacity:120},{Id:25,Name:'Gate8',leaf:true,iconCls:'sch-gate',Capacity:125},{Id:26,Name:'Gate9',leaf:true,iconCls:'sch-gate',Capacity:100},{Id:27,Name:'Gate10',leaf:true,iconCls:'sch-gate',Capacity:100}]}]}]}]}";
		return employeeTaskDAO.getEmployeeTaskData(startdate, enddate);
	}
	
}

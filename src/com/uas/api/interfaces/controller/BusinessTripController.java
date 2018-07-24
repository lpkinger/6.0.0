package com.uas.api.interfaces.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uas.api.interfaces.service.BusinessTripService;


@Controller
public class BusinessTripController {
	@Autowired
	private BusinessTripService businessTripService;
	
	@RequestMapping(value="/api/interfaces/UpdateBussinessTrip.action")//,method=RequestMethod.POST
	@ResponseBody
	public List<Map<String,Object>> UpdateBussinessTrip(HttpServletRequest request,HttpServletResponse response,HttpSession session,String emcode,String jsonStr,String master,int isLead,String otherContent) throws UnsupportedEncodingException {
		List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
		if(emcode.indexOf(",")>0) {
			String[] codes = emcode.split(",");
			for (String string : codes) {
				list.add(businessTripService.UpdateOrInsertBussinessTrip(string,jsonStr,master,isLead,otherContent));
			}
		}else {
			list.add(businessTripService.UpdateOrInsertBussinessTrip(emcode,jsonStr,master,isLead,otherContent));
		}
		return list;
	}


	@RequestMapping(value="/api/interfaces/getEmployee.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getEmployee(HttpServletRequest request,HttpSession session,String emcode,String master) {
		Map<String,Object> map=new HashMap<String,Object>();
		map.put("success", true);
		map.put("listdata",businessTripService.getEmployee(emcode,master));
		return map;
	}
	
	@RequestMapping(value="/api/interfaces/updateEmployee.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> updateEmployee(HttpServletRequest request,HttpSession session,String master,String params) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean bool = businessTripService.updateEmployee(master,params);
		if(bool) {
			map.put("success", true);
		}else {
			map.put("failure", "请检查账套名称是否正确！！");
		}
		return map;
	}
	
	@RequestMapping(value="/api/interfaces/getDepartment.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getDepartment(HttpServletRequest request,HttpSession session,String dept) {
		Map<String,Object> map=new HashMap<String,Object>();
		map =  businessTripService.getDepartment(dept);
		return map;
	}
	
	@RequestMapping(value="/api/interfaces/newDepartment.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> insertDepartment(HttpServletRequest request,HttpSession session,String dept,String deptname) {
		Map<String,Object> map=new HashMap<String,Object>();
		boolean bool = businessTripService.newDepartment(dept,deptname);
		if(bool) {
			map.put("success", true);
		}else {
			map.put("failure", "新增失败！");
		}
		return map;
	}
	
	@RequestMapping(value="/api/interfaces/getemp.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getemp(HttpServletRequest request,HttpSession session,String dept,String code) {
		Map<String,Object> map=new HashMap<String,Object>();
		map = businessTripService.getEmp(dept,code);
		return map;
	}
}

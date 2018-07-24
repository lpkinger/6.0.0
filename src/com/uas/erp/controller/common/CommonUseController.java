package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.uas.erp.model.Employee;
import com.uas.erp.model.commonuse.CommonUseItem;
import com.uas.erp.service.common.CommonUseService;
/**
 * 常用功能设置
 * 
 * @author zhuth
 * @time 2018年4月28日
 */
@Controller
@RequestMapping("/commonuse")
public class CommonUseController {

	@Autowired
	CommonUseService commonUseService;
	
	/**
	 * 导入常用功能项 
	 */
	@RequestMapping(value="/importAll.action")
	@ResponseBody
	public Map<String,Object> importAll(HttpSession session, String jsonstr){
		Employee employee=(Employee)session.getAttribute("employee");
		JSONArray data = JSON.parseArray(jsonstr);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonUseService.importAll(employee, data);
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 获得全部常用功能项
	 * */
	@RequestMapping(value="/getList.action")
	@ResponseBody
	public Map<String,Object> getList(HttpSession session){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<CommonUseItem> data = commonUseService.getList(employee);
		modelMap.put("success", true);
		modelMap.put("data", data);
	    return modelMap;
	}
	
	/**
	 * 添加常用功能项
	 */
	@RequestMapping(value="/add.action")
	@ResponseBody
	public Map<String,Object> add(HttpSession session, boolean group, String groupid, String itemid, String items, int index){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonUseService.add(employee, group, groupid, itemid, items, index);
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 修改常用功能项
	 */
	@RequestMapping(value="/modify.action")
	@ResponseBody
	public Map<String,Object> modify(HttpSession session, String groupid, String text, int index){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonUseService.modify(employee, groupid, text, index);
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 删除常用功能项
	 */
	@RequestMapping(value="/remove.action")
	@ResponseBody
	public Map<String,Object> remove(HttpSession session, String id){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		commonUseService.remove(employee, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 同步到所有子账套
	 */
	@RequestMapping(value="/synchronous.action")
	@ResponseBody
	public Map<String,Object> synchronous(HttpSession session, String sobs){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String, List<String>> masters = commonUseService.synchronous(employee, sobs.split(","));
		modelMap.put("success", true);
		modelMap.put("masters", masters);
		
		return modelMap;
	}
}

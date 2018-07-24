package com.uas.erp.controller.sys;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.sys.AlertDataService;


@Controller
public class AlertDataController {
	
	@Autowired
	private AlertDataService alertDataService;
	
	@RequestMapping("sys/alert/revertAlertData.action")  
	@ResponseBody 
	public Map<String, Object> revert(int id, String caller,String ad_cause, String ad_solution) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertDataService.revert(id, caller,ad_cause,ad_solution);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/confirmAlertData.action")  
	@ResponseBody 
	public Map<String, Object> confirm(int id, String caller,String ad_cause, String ad_solution) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertDataService.confirm(id, caller,ad_cause,ad_solution);
		resMap.put("success", true);
		return resMap;
	}
	
	@RequestMapping("sys/alert/getAlertData.action")
	@ResponseBody
	public Map<String, Object> getAlertData(HttpSession session,String condition,
									String likestr,Integer page,Integer limit) {	
		Map<String, Object> modelMap = new HashMap<String, Object >();
		Employee employee=(Employee)session.getAttribute("employee");
		List<Map<String, Object>> list = alertDataService.getAlertData(employee,condition,likestr, page,limit);
		//total是分页需要的字段
//		modelMap.put("total", alertDataService.getAlertDataTotal(employee, condition, likestr,page, limit));
		modelMap.put("data", list);
//		modelMap.put("count",alertDataService.getAlertDataCount(employee));
		modelMap.put("success", true);
		return modelMap;
	}
	
	@RequestMapping("sys/alert/revertDealAlertData.action")  
	@ResponseBody 
	public Map<String, Object> dealRevert(HttpSession session, String caller, String data) {
		Map<String, Object> resMap = new HashMap<String, Object>();
		alertDataService.dealRevert(caller,data);
		resMap.put("success", true);
		return resMap;
	}
}

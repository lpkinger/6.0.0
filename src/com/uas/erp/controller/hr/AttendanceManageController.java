package com.uas.erp.controller.hr;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.hr.AttendanceManageService;
@Controller
public class AttendanceManageController {
	@Autowired
	private AttendanceManageService attendanceManageService;
	@RequestMapping("/hr/attendance/result.action")  
	@ResponseBody 
	public Map<String, Object> result(String startdate,String enddate,boolean toAttendanceConfirm) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceManageService.result(startdate, enddate,toAttendanceConfirm);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 考勤确认
	 * */
	@RequestMapping("hr/attendance/AttendConfirm.action")  
	@ResponseBody 
	public Map<String, Object> AttendConfirm(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceManageService.AttendConfirm(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 考勤取消确认
	 * */
	@RequestMapping("hr/attendance/AttendResConfirm.action")  
	@ResponseBody 
	public Map<String, Object> AttendResConfirm(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		attendanceManageService.AttendResConfirm(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
}

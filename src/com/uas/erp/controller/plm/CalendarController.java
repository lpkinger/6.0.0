package com.uas.erp.controller.plm;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.CalendarService;
@Controller
public class CalendarController {
 @Autowired
 private CalendarService calendarService;
 @RequestMapping("plm/calendar/save.action")
 @ResponseBody
 public Map<String,Object> save(String addData,String updateData,String deleteData) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 calendarService.saveEvents(addData, updateData, deleteData);
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/calendar/getData.action")
 @ResponseBody
 public Map<String,Object> getCalendar(String caller,String emid, HttpSession session) throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 Employee employee = (Employee)session.getAttribute("employee");
	 modelMap.put("evts",calendarService.getCalendar(caller, emid, employee));
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/calendar/getMyData.action")
 @ResponseBody
 public Map<String,Object>getMyCalendar(String emid,String condition)throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("evts",calendarService.getMyCalendar(emid,condition));
	 modelMap.put("success",true);
	 return modelMap;
 }
 @RequestMapping("plm/calendar/getMyAgenda.action")
 @ResponseBody
 public Map<String,Object>getMyAgenda(String emid)throws Exception{
	 Map<String,Object> modelMap=new HashMap<String, Object>();
	 modelMap.put("evts",calendarService.getMyAgenda(emid));
	 modelMap.put("success",true);
	 return modelMap;
 }
}

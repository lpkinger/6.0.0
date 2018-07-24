package com.uas.opensys.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.model.Employee;
import com.uas.opensys.service.CurBaseService;
@Controller
public class CurBaseController {
	@Autowired
	private CurBaseService curBaseService;	
	@RequestMapping("/opensys/custserve.action")
	@ResponseBody
	public ModelAndView CustomerService(HttpSession session, HttpServletRequest request) {
		Map<String,Object> params=new HashMap<String,Object>();
		params.put("employee",(Employee)session.getAttribute("employee"));
		return new ModelAndView("opensys/customer/default",params);
	}
	@RequestMapping("/opensys/getCurSysnavigation.action")
	@ResponseBody
	public Map<String,Object> getCurSysnavigation(){
		Map<String,Object> modelMap=new HashMap<String, Object>();
		modelMap.put("data", curBaseService.getCurNavigation());
		modelMap.put("success", true);
		return modelMap;	
	}
	@RequestMapping("/opensys/getCurNotify.action")
	@ResponseBody
	public Map<String,Object> getCurNotify(String condition){
		Map<String, Object> modelMap = curBaseService.getCurNotify(condition);
		modelMap.put("success", true);
		return modelMap;	
	}
}

package com.uas.erp.controller.b2c;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Master;
import com.uas.erp.service.b2c.WelcomeService;
	/**
 * 获取、添加商城首页访问信息
 * 
 * @author wuyx
 *
 */
@Controller
public class WelcomeController {

	@Autowired
	private WelcomeService welcomeService;
	
	@RequestMapping("/b2b/main/getWelcomeStatus.action")
	@ResponseBody
	public Map<String, Object> getWelcomeStatus(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("success", welcomeService.getWelcomeStatus(employee));
		return modelMap;
	}
	@RequestMapping("/b2b/main/setWelcomeStatus.action")
	@ResponseBody
	public Map<String, Object> setWelcomeStatus(HttpSession session,String url) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("success", welcomeService.setWelcomeStatus(employee,url));
		return modelMap;
	}
	@RequestMapping("/b2b/main/isTureMaster.action")
	@ResponseBody
	public Map<String, Object> isTureMaster(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if(welcomeService.isTureMaster().equals("Yes")){
			modelMap.put("success", true);
		}else{
			modelMap.put("log", welcomeService.isTureMaster());
			modelMap.put("success", false);
		}
		return modelMap;
	}
}

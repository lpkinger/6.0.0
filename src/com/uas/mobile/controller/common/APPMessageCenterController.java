package com.uas.mobile.controller.common;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.APPMessageCenterService;

@Controller
public class APPMessageCenterController {

	@Autowired
	private APPMessageCenterService messageCenterService;
	
	@ResponseBody
	@RequestMapping("/mobile/queryEmNews.action")
	private Map<String,Object> queryEmNews(HttpServletRequest request, String emcode) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		ModelMap map = new ModelMap();
		map.put("allCount", messageCenterService.queryAllCount(emcode,""));
		map.put("success", true);
		map.put("sessionId", request.getSession().getId());
		map.put("listdata", messageCenterService.queryEmNews(emcode));
		return map;
	}
	@ResponseBody
	@RequestMapping("/mobile/queryEmNewsDetails.action")
	private Map<String,Object> queryEmNewsDetails(HttpServletRequest request, String emcode,String type) {
		Employee employee=(Employee)request.getSession().getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		
		ModelMap map = new ModelMap();
		map.put("allCount", messageCenterService.queryAllCount(emcode,type));
		map.put("success", true);
		map.put("sessionId", request.getSession().getId());
		map.put("listdata", messageCenterService.queryEmNewsDetails(emcode,type));
		return map;
	}
	
}

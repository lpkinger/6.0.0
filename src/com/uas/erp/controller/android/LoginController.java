package com.uas.erp.controller.android;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.dao.common.EmployeeDao;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.uu.UserService;

@Controller("androidLoginController")
public class LoginController {
	@Autowired
	private EmployeeDao employeeDao;
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	
	/**
	 * 根据session判断用户是否已登录
	 */
	@RequestMapping("/android/checkLogin.action")
	@ResponseBody
	public Map<String, Object> checkLogin(HttpServletRequest request,
			ModelMap modelMap, String u, String t, String d, String master) {
		if (u != null && t != null && d != null) {
			try {
				return fastLogin(request, modelMap, u, t, d, master, 1);//(****iphone设备默认不验证*所做修改*)
			} catch (Exception e) {
				return null;
			}
		}
		return null;
	}

	private Map<String, Object> fastLogin(HttpServletRequest request,
			ModelMap modelMap, String u, String t, String d, String master, int device) {//(****iphone设备默认不验证*所做修改*)
		
		try {
			HttpSession session = request.getSession();
			if (master == null)
				master = BaseUtil.getXmlSetting("defaultSob");
			String res = null;
			if(device != 2) {//(****iphone设备默认不验证*所做修改*)
				res = employeeService.loginWithEm(master, u, userService.decryptPassword(t, d), getIpAddr(request), true,null);
			}
			if (res == null) {// 表示登录成功
				modelMap.put("success", true);
				Employee employee = employeeService.getEmployeeByName(u);
				session.setAttribute("employee", employee);
				employee.setEm_master(master);
				employee.setCurrentMaster(getMaster(master));
				Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
				session.setAttribute("em_master", master);
				session.setAttribute("master", master);
				session.setAttribute("em_name", employee.getEm_name());
				session.setAttribute("em_code", employee.getEm_code());
				session.setAttribute("language", "zh_CN");
				session.setAttribute("employee", employee);
				session.setAttribute("em_depart", employee.getEm_depart());
				session.setAttribute("en_uu", enterprise.getEn_uu());
				session.setAttribute("en_name", enterprise.getEn_Name());
				session.setAttribute("em_uu", employee.getEm_id());
				session.setAttribute("_mobile", true);// 表示移动设备登录
				session.setAttribute("username", u);
				modelMap.put("em_name", employee.getEm_name());
			} else {
				modelMap.put("success", false);
				modelMap.put("reason", res);
			}
			return modelMap;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * 获取客户端IP
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0
				|| "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
															// = 15
			if (ipAddress.indexOf(",") > 0) {
				ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
			}
		}
		if (ipAddress != null && "0:0:0:0:0:0:0:1".equals(ipAddress)) {// window7系统下,用localhost访问时,ip会变成0:0:0:0:0:0:0:1
			ipAddress = "127.0.0.1";
		}
		return ipAddress;
	}

	/**
	 * 根据session判断用户是否已登录,如果没登陆则登陆 然后跳到指定页面
	 * 先由拦截器 com/uas/erp/core/interceptor/AndroidInterceptor进行拦截验证
	 * @param device 区别设备，1 为Android， 2 为iPhone 
	 * @throws IOException
	 */
	@RequestMapping("/android/jprocessDeal.action")
	public void jprocessDeal(HttpServletRequest request,
			HttpServletResponse response, HttpSession session,
			ModelMap modelMap, String u, String t, String d, String url, String master, String device)//(****iphone设备默认不验证*所做修改*)
			throws IOException { 
		int de = 1;//默认为Androidd登录(****iphone设备默认不验证*所做修改*)
		if(device != null && !device.equals("")){//默认为Androidd登录(****iphone设备默认不验证*所做修改*)
			de = Integer.parseInt(device);	
		};
		fastLogin(request, modelMap, u, t, d, master, de);//(****iphone设备默认不验证*所做修改*)
		response.sendRedirect(url);
	}
	
	public Master getMaster(String name) {
		List<Master> masters = enterpriseService.getMasters();
		if (masters != null && name != null) {
			for (Master m : masters) {
				if (name.equals(m.getMa_name())) {
					return m;
				}
			}
		}
		return null;
	}
}

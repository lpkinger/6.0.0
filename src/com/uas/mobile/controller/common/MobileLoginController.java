package com.uas.mobile.controller.common;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.MobileSessionContext;
import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Master;
import com.uas.erp.service.common.AccountCenterService;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.uu.UserService;
import com.uas.sso.entity.UserView;

/**
 * UAS移动平台登录相关请求处理
 * 
 */
@Controller("mobileLoginController")
public class MobileLoginController {
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	@Autowired
	private AccountCenterService accountCenterService;

	static final String TEL_REGEXP = "^((\\(\\d{3}\\))|(\\d{3}\\-))?(13|15|17|18)\\d{9}$";

	@RequestMapping("/mobile/login.action")
	@ResponseBody
	public Map<String, Object> login(HttpServletRequest request, HttpServletResponse response, HttpSession session, String username,
			String password, String master, String token, String enuu) {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Master masterObj = null;
		if ((token != null)||(username != null && password != null && master != null)) {
			String res = null;
			if(token!=null){		//新版账号中心 ，通过token登录	
				if(enuu!=null&&!enuu.trim().equals("")){					
					master = enterpriseService.getMasterByUU(enuu);
				}
				if(master == null){  //如果获取的master为空，则使用默认账套
					master = BaseUtil.getXmlSetting("defaultSob");
				}
				masterObj = enterpriseService.getMasterByName(master);
				res = employeeService.loginWithToken(master, token, getIpAddr(request), true, null);
			}else{
				res = employeeService.loginWithEm(master, username, password, getIpAddr(request), true,null);
			}
			if (res == null) {
				modelMap.put("success", true);
				Employee employee = null;
				if(token!=null){   //新版账号中心，通过token获取手机号
					try {
						UserView user = accountCenterService.getUserByToken(token);
						employee = employeeService.getEmployeeByEmTel(user.getMobile());
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else if(username!=null){  //旧版账号中心
					if (username.matches(TEL_REGEXP)) {
						employee = employeeService.getEmployeeByEmTel(username);
					} else {
						employee = employeeService.getEmployeeByName(username);
					}
				}
				// Employee employee = employeeService.getEmployeeByName(username);
				employeeService.setMoLastLoginTime(employee, new Date());
				Enterprise enterprise = enterpriseService.getEnterprise();
				if (enterprise != null) {
					session.setAttribute("en_uu", enterprise.getEn_uu());
					session.setAttribute("en_name", enterprise.getEn_Name());
					session.setAttribute("en_email", enterprise.getEn_Email());
					modelMap.put("uu", enterprise.getEn_uu());
				}
				employee.setEm_master(master);
				employee.setCurrentMaster(getMaster(master));
				session.setAttribute("employee", employee);
				session.setAttribute("language", "zh_CN");
				modelMap.put("sessionId", session.getId());
				modelMap.put("erpaccount", employee.getEm_code());
				modelMap.put("emname", employee.getEm_name());
				modelMap.put("EN_ADMIN", enterprise.getEn_Admin());
				modelMap.put("EN_URL", enterprise.getEn_Url());
				modelMap.put("EN_PRINTURL", enterprise.getEn_printurl());
				modelMap.put("EN_INTRAJASPERURL", enterprise.getEn_intrajasperurl());
				modelMap.put("EN_EXTRAJASPERURL", enterprise.getEn_extrajasperurl());
				MobileSessionContext.getInstance().createSession(session);
				modelMap.put("success", true);
				//TODO:manageid masterId,ma_name master
				if(masterObj!=null){
					modelMap.put("masterId", masterObj.getMa_manageid());
					modelMap.put("master", masterObj.getMa_name());
				}
				employeeService.checkAppToken(session.getId(),session.getId(),employee.getEm_code(),0);
			} else {
				modelMap.put("success", false);
				modelMap.put("reason", res);
			}					
		} else {
			modelMap.put("success", false);
			modelMap.put("reason", "非正常请求");
		}
		return modelMap;
	}

	/**
	 * 获取系统所有账套
	 */
	@RequestMapping("/mobile/getAllMasters.action")
	@ResponseBody
	public Map<String, Object> getMasters() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (isSaas()) {// saas独立使用，不需返回账套信息
			modelMap.put("masters", new JSONArray());
		} else {
			modelMap.put("masters", enterpriseService.getMasters());
		}
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 是否saas域名访问
	 * 
	 * @return
	 */
	private boolean isSaas() {
		return BaseUtil.getXmlSetting("saas.domain") != null;
	}

	/**
	 * 获取请求客户端的IP地址
	 * 
	 * @param request
	 *            发送请求
	 * @return String Ip地址
	 */
	public String getIpAddr(HttpServletRequest request) {
		String ipAddress = null;
		ipAddress = request.getHeader("X-Forwarded-For");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
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
	 * 取当前账套信息
	 * 
	 * @param name
	 * @return Master
	 */
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

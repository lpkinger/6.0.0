package com.uas.erp.core.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.dao.BaseDao;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;


public class PdaMobileInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private BaseDao baseDao;
	
	final static String INSERT_LOGINFO = "insert into loginfo (id,indate,sip,usname,uscode,versioncode,terminaltype)values (loginfo_seq.nextval,sysdate,?,?,?,?,?)";
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {	
		String username = request.getHeader("j_username");
		String password = request.getHeader("j_password");
		String versioncode = request.getHeader("versioncode");
		String master = request.getHeader("master");
		String uri = request.getRequestURI().toString();
		if(!uri.contains("api/pda/getAllMasters.action") && !uri.contains("api/pda/login.action")){
			Object obj = request.getSession().getAttribute("employee");
			if (obj == null && master != null) {
				String res = employeeService.loginWithEm(master, username, password, null, true,null);
				if (res != null) {
					ServletOutputStream out = response.getOutputStream();
					out.write(res.getBytes("utf-8") );
					out.flush();
					out.close();
					return false;
			   }else{
				   Employee employee = (Employee) CreateSessionEmployee(username);
					// 记录到当前线程里面
					SystemSession.setUser(employee);
					request.getSession().setAttribute("employee", employee);
					request.getSession().setAttribute("language", "zh_CN");
					/*baseDao.execute(INSERT_LOGINFO,getIpAddr(request),employee.getEm_name(),username,versioncode,"PDA");*/
			   }
			}
		}
		/*if(username != null && password != null ){	
			String res = employeeService.loginWithEm(master, username, password, null, true,null);
			if (res != null) {
				ServletOutputStream out = response.getOutputStream();
				out.write(res.getBytes("utf-8") );
				out.flush();
				out.close();
				return false;
		   }
		}*/
		return super.preHandle(request, response, handler);
	}
	private Employee CreateSessionEmployee(String sessionUser) {
		return employeeService.getEmployeeByName(sessionUser);
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
}

package com.uas.erp.core.interceptor;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.uu.UserService;

/**
 * 对android设备的请求进行拦截，每次请求验证用户信息
 * 
 * @author yingp
 */
public class AndroidInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String u = request.getParameter("u");
		String t = request.getParameter("t");
		String d = request.getParameter("d");
		String master = request.getParameter("master");
		String de = request.getParameter("device");
		String res = null;
		if(!"2".equals(de)) {
			if (u == null || t == null || d == null) {
				res = "非正常请求信息";
			} else {
				res = checkUserInfo(master, u, t, d);
			}
		}
		if (res != null) {
			ServletOutputStream out = response.getOutputStream();
			out.write(res.getBytes("utf-8"));
			out.flush();
			out.close();
			return false;
		}
		return super.preHandle(request, response, handler);
	}

	/**
	 * @param u
	 *            username
	 * @param t
	 *            token
	 * @param d
	 *            date
	 * @return
	 */
	private String checkUserInfo(String master, String u, String t, String d) {
		String res = employeeService.loginWithEm(master, u,
				userService.decryptPassword(t, d), null, true,null);
		
		return res;
	}
}

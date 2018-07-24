package com.uas.erp.core.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.uas.erp.model.Employee;

/**
 * 对在线考试系统的拦截
 * 
 * @author yingp
 */
public class ExaminationInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Object obj = request.getSession().getAttribute("employee");
		if (obj == null) {
			obj = dynamicCreateEmployee();
			request.getSession().setAttribute("employee", obj);
		}
		return super.preHandle(request, response, handler);
	}

	private Employee dynamicCreateEmployee() {
		Employee employee = new Employee();
		employee.setEm_id(0);
		employee.setEm_code("customer");
		employee.setEm_type("admin");
		return employee;
	}

}

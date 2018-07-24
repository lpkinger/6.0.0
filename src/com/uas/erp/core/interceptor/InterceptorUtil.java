package com.uas.erp.core.interceptor;

import javax.servlet.http.HttpServletRequest;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;

public class InterceptorUtil {

	/**
	 * opensys
	 */
	public static final String OPENSYS_REGEXP = ".*\\/opensys\\/.*";

	/**
	 * 无需拦截
	 * 
	 * @param request
	 * @return
	 */
	public static boolean isOpenSys(HttpServletRequest request) {
		String referer = request.getHeader("Referer");
		return referer != null && referer.matches(OPENSYS_REGEXP);
	}

	/**
	 * 不限制权限的请求
	 * 
	 * @param request
	 * @return
	 */
	public static boolean noControl(HttpServletRequest request) {
		String control = request.getParameter("_noc");// 不限制权限
		if (control != null && "1".equals(control)) {
			return true;
		}
		Object _control = request.getAttribute("_noc");// 不限制权限
		if (_control != null && "1".equals(_control.toString())) {
			return true;
		}
		return false;
	}

	/**
	 * 校验虚拟用户
	 * 
	 * @param request
	 * @return
	 */
	public static boolean checkVirtual(HttpServletRequest request,Employee employee) {
		if( employee.isAdminVirtual() || (employee.isCustomerVirtual() && isOpenSys(request))) return true;
		else if(employee.isCustomerVirtual() && employee.getEm_id()!=-99999){
		 return true;
		}else if(employee.isCustomerVirtual() && !isOpenSys(request)){
			BaseUtil.showError("ERR_NETWORK_SESSIONOUT");
			return false;
		}else return false;
	}
	
	//是否需要权限控制
	public static boolean noControl(HttpServletRequest req, Employee employee){
		if (noControl(req)||"admin".equals(employee.getEm_type()) || InterceptorUtil.isOpenSys(req)){
			return true;
		}
		return false;
	}
}

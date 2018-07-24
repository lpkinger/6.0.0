package com.uas.erp.core.support;

import java.util.ArrayList;
import java.util.List;

import com.uas.erp.core.CollectionUtil;
import com.uas.erp.core.interceptor.UserInterceptor;
import com.uas.erp.model.Employee;

/**
 * 每次请求服务器时，用户信息、语言等存放在本次线程中
 * 
 * @author yingp
 * @see UserInterceptor
 * 
 */
public class SystemSession {

	private static ThreadLocal<Employee> user = new InheritableThreadLocal<Employee>();

	private static ThreadLocal<String> lang = new InheritableThreadLocal<String>();

	private static ThreadLocal<List<String>> errors = new ThreadLocal<List<String>>();

	public static void setUser(Employee employee) {
		user.set(employee);
	}

	/**
	 * @return 当前用户
	 */
	public static Employee getUser() {
		return user.get();
	}

	public static void setLang(Object language) {
		lang.set(language == null ? null : language.toString());
	}

	/**
	 * @return 当前用户选择的语言
	 */
	public static String getLang() {
		return lang.get();
	}

	/**
	 * 针对出错后，暂时不中断程序，而是临时存放error信息，完成所有操作后，再返回给客户端
	 * 
	 * @param error
	 */
	public static void appendError(String error) {
		List<String> es = errors.get();
		if (es == null)
			es = new ArrayList<String>();
		es.add(error);
		errors.set(es);
	}

	public static String getErrors() {
		List<String> es = errors.get();
		if (es != null && es.size() > 0) {
			return CollectionUtil.toString(es, "\n");
		}
		return null;
	}

	public static void clearErrors() {
		errors.set(null);
	}

	public static void clear() {
		user.set(null);
		lang.set(null);
		errors.set(null);
	}

}

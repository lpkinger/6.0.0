package com.uas.erp.dao.common;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.uas.erp.model.Interceptors;

public interface InterceptorDao {

	/**
	 * 配置的逻辑
	 * 
	 * @param caller
	 * @return
	 */
	List<Interceptors> getInterceptorsByCaller(String caller,HttpSession session);

	/**
	 * 配置的逻辑
	 * 
	 * @param sob
	 * @param caller
	 * @param type
	 * @param turn
	 * @param enable
	 * @return
	 */
	List<Interceptors> getInterceptorsByCallerAndType(String sob, String caller, String type, short turn, short enable);

}

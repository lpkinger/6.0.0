package com.uas.erp.service.ma;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import com.uas.erp.model.Interceptors;

public interface InterceptorService {

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
	 * @param caller
	 * @param type
	 * @param turn
	 * @return
	 */
	List<Interceptors> getInterceptorsByCallerAndType(String caller, String type, String turn);
	
	/**
	 * 修改配置的逻辑
	 * 
	 * @param updated
	 */
	void saveInterceptors(List<Map<Object, Object>> updated);

	List<Interceptors> getInterceptorsByCondition(String condition);

}

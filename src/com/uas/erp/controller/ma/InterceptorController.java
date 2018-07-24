package com.uas.erp.controller.ma;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Interceptors;
import com.uas.erp.service.ma.InterceptorService;

/**
 * 逻辑配置
 * 
 * @author yingp
 * 
 */
@Controller
@RequestMapping("/ma/setting")
public class InterceptorController {

	@Autowired
	private InterceptorService interceptorService;

	@RequestMapping(value="/interceptors.action" ,method = RequestMethod.GET)
	@ResponseBody
	public List<Interceptors> getInterceptorsByCaller(String caller,HttpSession session) {
		return interceptorService.getInterceptorsByCaller(caller,session);
	}
	@RequestMapping(value="/interceptors.action" ,method = RequestMethod.POST)
	@ResponseBody
	public void saveInterceptors(String updated) {
		interceptorService.saveInterceptors(BaseUtil.parseGridStoreToMaps(updated));
	}
	@RequestMapping(value="/getInterceptorsByCondition.action" ,method = RequestMethod.GET)
	@ResponseBody
	public List<Interceptors> getInterceptorsByCondition(String condition) {
		return interceptorService.getInterceptorsByCondition(condition);
	}

}

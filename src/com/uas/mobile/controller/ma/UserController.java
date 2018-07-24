package com.uas.mobile.controller.ma;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.mobile.service.UserService;

/**
 * 主要负责处理移动客户端中对于用户进行管理的请求
 * @author Administrator
 *
 */
@Controller("mobileUserController")
public class UserController {

	@Autowired
	private UserService mobileUserService;
	
	/**
	 * 获取使用过移动客户端的用户
	 * @param request
	 * @param response
	 * @param session
	 * @return data{用户名，用户编号，最后登陆时间，所在账套，账套名称}
	 */
	@RequestMapping("/mobile/ma/user/getLoginedEmployees.action")
	@ResponseBody
	public Map<String, Object> getLoginedEmployees(HttpServletRequest request, HttpServletResponse response,
			HttpSession session) {
		response.setContentType("application/json");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", mobileUserService.getLoginedEmployees());
		return modelMap;
	}
}

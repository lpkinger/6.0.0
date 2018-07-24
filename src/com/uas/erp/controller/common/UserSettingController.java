package com.uas.erp.controller.common;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 用户选项及基本设置
 * 
 * @author yingp
 *
 */
@Controller
@RequestMapping("/common/usersetting")
public class UserSettingController {

	/**
	 * 开发者模式
	 */
	@RequestMapping(value = "/develop.action", method = RequestMethod.POST)
	@ResponseBody
	public void updateDevelopSetting(HttpServletRequest request, HttpSession session, Boolean debug) {
		// 切换是否显示堆栈信息
		if (debug != null) {
			session.setAttribute("user.setting.debug", debug.booleanValue());
		}
	}

	/**
	 * 开发者模式
	 */
	@RequestMapping(value = "/develop.action", method = RequestMethod.GET)
	@ResponseBody
	public ModelMap getDevelopSetting(HttpServletRequest request, HttpSession session) {
		ModelMap map = new ModelMap();
		// 是否显示堆栈信息
		/*map.put("debug", session.getAttribute("user.setting.debug"));*/
		//自动显示堆盏信息
		session.setAttribute("user.setting.debug",true);
		return map;
	}

}

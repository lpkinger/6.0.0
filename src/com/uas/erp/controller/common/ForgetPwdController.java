package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.PasswordEncryUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.ForgetPwdService;
import com.uas.erp.service.ma.EncryptService;

@Controller
public class ForgetPwdController {

	@Autowired
	private ForgetPwdService forgetPwdService; 
	
	/**
	 * 返回忘记密码页面
	 * @param request
	 * @return
	 */
	@RequestMapping("/common/forgetPwd.action")
	public String forgetPws(HttpServletRequest request){
		return "common/forgetPwd";
	}
	
	/**
	 * 忘记密码，发送邮件
	 * @param request
	 * @param em_code
	 * @param em_name
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/common/sendChangePwdEmail.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> sendChangePwdEmail(HttpServletRequest request, String em_code, String em_name) throws Exception{
		return forgetPwdService.sendChangePwdEmail(request, em_code, em_name);
	}
	
	/**
	 * 修改密码界面
	 * @param key
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/common/changePwd.action")
	public ModelAndView changePassword(String key) throws Exception{
		ModelAndView model = new ModelAndView();
		Map<String,Object> map = forgetPwdService.changePwd(key);
		model.addObject("result", map);
		model.setViewName("common/changePwd");
		return model;
	}
	
	/**
	 * 修改密码
	 * @param password
	 * @param em_code
	 * @return
	 */
	@RequestMapping(value = "/common/changePassword.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> updatePassword(String password, String em_code){
		Map<String, Object> map = new HashMap<String, Object>();
		map = forgetPwdService.updatePassword(password, em_code);
		return map;
	}
}

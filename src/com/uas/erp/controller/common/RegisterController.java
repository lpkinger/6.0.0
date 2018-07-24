package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.uu.UserService;

/**
 * 处理企业注册请求
 * @author yingp
 * @date 2012-7-26 0:32:14
 */
@Controller
public class RegisterController {
	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private UserService userService;
	
	/**
	 * 根据en_Name判断该企业是否已注册
	 * @param en_Name 企业中文名称
	 */
	@RequestMapping("/common/checkEnName.action")  
	@ResponseBody 
	public Map<String, Object> checkEnName(@RequestBody String en_Name) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", enterpriseService.checkEnterpriseName(en_Name));
		return modelMap;
	}
	/**
	 * 保存所有注册信息
	 * @param enterprise 企业注册的信息
	 */
	@RequestMapping("/common/register.action")  
	@ResponseBody 
	public Map<String, Object> register(@RequestBody Enterprise enterprise) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		enterpriseService.saveEnterprise(enterprise);
		return modelMap;
	}
	/**
	 * 系统所有账套
	 */
	@RequestMapping("/system/getMasters.action")  
	@ResponseBody 
	public Map<String, Object> getMasters(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("masters", enterpriseService.getMasterNames());
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 账套登录
	 */
	@RequestMapping("/system/checkMaster.action")  
	@ResponseBody 
	public Map<String, Object> checkMaName(String ma_name, String ms_pwd) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", enterpriseService.checkMasterNamePwd(ma_name, ms_pwd));
		return modelMap;
	}
	
	/**
	 * 初始UU号
	 */
	@RequestMapping("/system/uu/init.action")  
	@ResponseBody 
	public Map<String, Object> initUU(String ma_name, String ms_pwd) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<Employee> employees = employeeService.getEmployeesByCondition("em_uu is not null");
		for(Employee employee:employees) {
			userService.createUser(String.valueOf(employee.getEm_uu()), employee.getEm_password());
		}
		modelMap.put("success", true);
		return modelMap;
	}
}

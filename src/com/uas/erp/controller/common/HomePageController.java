package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.model.Enterprise;
import com.uas.erp.model.Weather;
import com.uas.erp.service.common.EmployeeService;
import com.uas.erp.service.common.EnterpriseService;
import com.uas.erp.service.common.WorkBenchService;

/**
 * 处理用户登录成功后，进入ERP主页之前的逻辑
 * 
 * @author yingp
 */
@Controller
public class HomePageController {

	@Autowired
	private EnterpriseService enterpriseService;
	@Autowired
	private EmployeeService employeeService;
	@Autowired
	private WorkBenchService workBenchService;

	/**
	 * 拿到与用户有关的一些信息 例如企业信息，个人信息，组织信息，消息，工作，邮件等。 也可以单独去加载
	 */
	@RequestMapping("/common/homePage.action")
	public String getUserInfo(HttpSession session, ModelMap modelMap) {
		if (session.getAttribute("username") == null) {
			return "redirect:../index.jsp";
		}
		String username = (String) session.getAttribute("username");
		Employee employee = employeeService.getEmployeeByName(username);
		Enterprise enterprise = enterpriseService.getEnterpriseById(employee.getEm_enid());
		session.setAttribute("employee", employee);
		session.setAttribute("enterprise", enterprise);
		session.setAttribute("en_uu", enterprise.getEn_Id());
		session.setAttribute("en_name", enterprise.getEn_Name());
		session.setAttribute("em_uu", employee.getEm_id());
		session.setAttribute("em_name", employee.getEm_name());
		return "redirect:../jsps/common/main.jsp";
	}

	/**
	 * 获取工作台设置
	 */
	@RequestMapping("/common/getWorkBench.action")
	@ResponseBody
	public Map<String, Object> getWorkBench(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("benchs", workBenchService.getWorkBench(employee));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改工作台设置
	 */
	@RequestMapping("/common/setWorkBench.action")
	@ResponseBody
	public Map<String, Object> setWorkBench(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		workBenchService.setWorkBench(employee, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 获取快捷栏设置
	 */
	@RequestMapping("/common/getShortCut.action")
	@ResponseBody
	public Map<String, Object> getShortCut(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("short", workBenchService.getShortCut(employee));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改快捷栏设置
	 */
	@RequestMapping("/common/setShortCut.action")
	@ResponseBody
	public Map<String, Object> setShortCut(HttpSession session, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		workBenchService.setShortCut(employee, data);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 常用模块
	 */
	@RequestMapping("/common/getCommonUse.action")
	@ResponseBody
	public Map<String, Object> getCommonUse(HttpSession session,Integer count) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("commonuse", workBenchService.getCommonUses(employee,count));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置常用模块
	 */
	@RequestMapping("/common/setCommonUse.action")
	@ResponseBody
	public Map<String, Object> setCommonUse(HttpSession session,Integer id, String url, String addUrl, Integer  count,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		workBenchService.setCommonUse(id,url,addUrl,employee,caller);
		modelMap.put("commonuse", workBenchService.getCommonUses(employee,count));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除常用模块
	 */
	@RequestMapping("/common/deleteCommonUse.action")
	@ResponseBody
	public Map<String, Object> deleteCommonUse(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workBenchService.deleteCommonUse(id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改常用模块
	 */
	@RequestMapping("/common/updateCommonUse.action")
	@ResponseBody
	public Map<String, Object> updateCommonUse(HttpSession session, int id, int type,Integer count) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		workBenchService.updateCommonUse(employee, id, type);
		modelMap.put("commonuse", workBenchService.getCommonUses(employee,count));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 常用模块锁定/解锁
	 */
	@RequestMapping("/common/lockCommonUse.action")
	@ResponseBody
	public Map<String, Object> lockCommonUse(HttpSession session, int id, int type,Integer count) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		workBenchService.lockCommonUse(employee, id, type);
		modelMap.put("commonuse", workBenchService.getCommonUses(employee,count));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取指定城市的天气
	 */
	@RequestMapping("/common/getWeather.action")
	@ResponseBody
	public Map<String, Object> getWeather(HttpSession session, String city, String day) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("weather", Weather.getweather(city, day));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 获取桌面提醒设置
	 * */
	@RequestMapping("/common/getdesktopremind.action")
	@ResponseBody
	public Map<String, Object> getDeskTopRemind(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("DtRemaind", (employee.getEm_dtremind()!=null && employee.getEm_dtremind()==-1));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置桌面是否提醒
	 * */
	@RequestMapping("/common/setdesktopremind.action")
	@ResponseBody
	public Map<String, Object> setDeskTopRemind(HttpSession session, int remind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		employeeService.setDeskTopRemind(employee, remind);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	
	
	/**
	 * 获取消息提醒设置
	 * */
	@RequestMapping("/common/getMsgSet.action")
	@ResponseBody
	public Map<String, Object> getMsgSet(HttpSession session) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("IsRemaind", employee.getEm_remind() == 1);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 设置消息是否提醒
	 * */
	@RequestMapping("/common/setMsgRemaind.action")
	@ResponseBody
	public Map<String, Object> setMsgRemaind(HttpSession session, int remind) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		employeeService.setMsgRemaind(employee, remind);
		modelMap.put("success", true);
		return modelMap;
	}
   /**
    * 根据页面设置条数获取相关数据
    * */
	@RequestMapping("/common/home/getDatas.action")
	@ResponseBody
	public Map<String,Object> getDatas(HttpSession session,String type){		
		Map<String, Object> modelMap = new HashMap<String, Object>();
		workBenchService.getDatas(type);
		return modelMap;
	}
}

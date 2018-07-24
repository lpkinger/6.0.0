package com.uas.erp.controller.salary;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.web.DocumentConfig;
import com.uas.erp.core.web.ExcelViewUtils;
import com.uas.erp.model.Employee;
import com.uas.erp.service.salary.SalaryService;


@Controller
public class SalaryController {

	@Autowired
	private SalaryService salaryService;
	
	/**
	 * 发送工资条
	 */
	@RequestMapping("/salary/sendMsg.action")
	@ResponseBody
	public Map<String,Object>sendMsg(HttpSession session,String ilid,String text,String date,Integer signature){
		Map<String,Object> map=new HashMap<String,Object>();
		salaryService.sendMsg(ilid,text,date,signature);
		map.put("success", true);
		return map;
	}
	
	/**
	 * 获取历史记录
	 */
	@RequestMapping("/salary/getHistory.action")
	@ResponseBody
	public Map<String,Object>getHistory(HttpSession session,String date, String condition,int page,int pageSize){
		int end= page * pageSize;
		int start = (page - 1)  * pageSize;
		Map<String,Object> map=salaryService.getHistory(date, condition, start, end);
		map.put("success", true);
		return map;
	}
	
	
	/**
	 * 保存工资条
	 */
	@RequestMapping("/salary/saveSalary.action")
	@ResponseBody
	public Map<String,Object> saveSalary(HttpSession session, Integer id, Integer start, Integer end,String date,String type){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		salaryService.toFormalData(employee,date, id, start, end,type);
		return modelMap;
	}
	
	/**
	 * 重发工资条
	 */
	@RequestMapping("/salary/resend.action")
	@ResponseBody
	public Map<String,Object>reSend(HttpSession session,String grid){
		Map<String,Object> model=new HashMap<String,Object>();
		salaryService.reSend(grid);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 删除工资条
	 */
	@RequestMapping("/salary/deleteData.action")
	@ResponseBody
	public Map<String,Object>deleteData(HttpSession session,String ids){
		Map<String,Object> model=new HashMap<String,Object>();
		salaryService.deleteData(ids);
		model.put("success", true);
		return model;
	}
	
	/**
	 * 下载内容
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping("/salary/exportAllHis.xls")
	public ModelAndView exportAll(HttpSession session,String date,String title)throws IOException{
		Employee employee = (Employee) session.getAttribute("employee");
		Map<String,Object> salary=salaryService.exportAllHis(date);
		DocumentConfig config =(DocumentConfig)salary.get("config");
		List<Map<String, Object>> datas=(List<Map<String,Object>>)salary.get("datas");
		return new ModelAndView(ExcelViewUtils.getView(config,datas, new String(title.getBytes("utf-8"), "iso8859-1"), employee));
		}
	
	/**
	 * 验证码
	 */
	@RequestMapping("/salary/verificationCode.action")
	@ResponseBody
	public Map<String,Object>verificationCode(HttpSession session,String phone,String  type){
		Map<String,Object> model= salaryService.verify(phone, type);
		if("login".equals(type)){
			session.setAttribute("login-code", model.get("vecode"));
		}else{
			session.setAttribute("modify-code", model.get("vecode"));
		}	
		model.remove("vecode");
		return model;		
	}
	
	/**
	 * 登录工资条
	 */
	@RequestMapping("/salary/login.action")
	@ResponseBody
	public Map<String,Object>login(HttpSession session,String emcode,String  password,String phonecode){
		System.out.println(session.getAttribute("login-code"));
		Map<String,Object> model=salaryService.login(emcode, password,phonecode,session.getAttribute("login-code"));
		if(model.get("reason")==null){
			session.removeAttribute("login-code");
		}
		session.setAttribute("salary", model.get("success"));
		return model;
	}
	
	/**
	 * 修改密码
	 */
	@RequestMapping("/salaryNote/changePwd/modify.action")
	@ResponseBody
	public Map<String,Object>changePwd(HttpSession session,String emcode,String password,String phonecode){
		System.out.println(session.getAttribute("modify-code"));
		Map<String,Object> model=salaryService.modifyPwd(emcode, password,phonecode,session.getAttribute("modify-code"));
		if(model.get("reason")==null){
			session.removeAttribute("modify-code");
		}
		return model;
	}
	
	/**
	 * 获取消息记录
	 */
	@RequestMapping("/salaryMsg/getMessgeLog.action")
	@ResponseBody
	public Map<String,Object>getMessgeLog(HttpSession session,int page,int start,int limit){
		return 	salaryService.getMessgeLog(page,start,limit);
	}
	
	/**
	 * 预约时间保存
	 */
	@RequestMapping("/salary/saveDate.action")
	@ResponseBody
	public Map<String,Object>saveDate(String ilid,String date,String text,Integer signature ){
		Map<String,Object> model=new HashMap<String,Object>();
		salaryService.saveDate(ilid,date,text,signature);
		model.put("success", true);
		return model;
	}
}

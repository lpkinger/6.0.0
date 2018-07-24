package com.uas.erp.controller.common;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.DeskTop;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.DeskTopService;
@Controller
@RequestMapping("/common/desktop")
public class DeskTopController {
	@Autowired
	private DeskTopService deskTopService;
	/**
	 * 获取个人桌面设置
	 * */
	@RequestMapping(value="/getOwner.action" ,method = RequestMethod.GET)
	@ResponseBody
	public List<DeskTop> getOwner() {
		Employee employee=SystemSession.getUser();
		return deskTopService.getOwner(employee);
	}
	/**
	 * 设置显示条数
	 * */
	@RequestMapping(value="/setTotalCount.action",method=RequestMethod.POST)
	@ResponseBody
	public String setTotalCount(int count,String type){
		return deskTopService.setTotalCount(count,type);
	}
	/**
	 * 工作台设置
	 * */
	@RequestMapping(value="/setDeskTop.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> setDeskTop(String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		deskTopService.setDeskTop(param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改桌面位置
	 * */
	@RequestMapping(value="/setDetno.action",method=RequestMethod.POST)
	@ResponseBody
	public String setDetno(String nodes){
		return deskTopService.setDetno(nodes);
	}
	/**
	 * 通过表单配置获取相应数据
	 * */
	@RequestMapping(value="/getData.action" ,method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getData(String caller,String condition,String orderby,Integer count) {
	   Map<String,Object> map=deskTopService.getData(caller,condition,orderby,count);
	   map.put("success",true);
	   return map;
	}
	/**
	 * 首页--流程数据
	 * @param count 数据条数
	 * @param type  数据类型
	 * @param model 其它参数
	 * */
	@RequestMapping(value="/process/{type}.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getDeskProcess(HttpSession session,int count,@PathVariable("type") String type,Model model,Integer isMobile){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String,Object> map=deskTopService.getDeskProcess(count,type,model,isMobile,employee);
		map.put("success",true);
		map.put("sessionId", session.getId());
	    return map;
	}
	/**
	 * 首页--RDM流程数据
	 * @param count 数据条数
	 * @param type  数据类型
	 * @param model 其它参数
	 * */
	@RequestMapping(value="/flow/{type}.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getDeskFlow(HttpSession session,int count,@PathVariable("type") String type,Model model,Integer isMobile){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String,Object> map=deskTopService.getDeskFlow(count,type,model,isMobile,employee);
		map.put("success",true);
		map.put("sessionId", session.getId());
	    return map;
	}
	/**
	 * 通知--内部通知
	 * */
	@RequestMapping(value="/note/inform.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getNote_Inform(HttpServletRequest request,Integer count,Integer page,Integer pageSize){
		int end = (page!=null && pageSize!=null)? page*pageSize: count;
		int start =(page!=null && pageSize!=null)? end - pageSize:0;
		Map<String,Object> map=deskTopService.getNote_Inform(start,end);
		map.put("success",true);
		map.put("sessionId", request.getSession().getId());
	    return map;
	}
	/**
	 * 通知--行政公告
	 * */
	@RequestMapping(value="/note/notice.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getNote_Notice(HttpServletRequest request,Integer count,Integer page,Integer pageSize){
		int end = (page!=null && pageSize!=null)? page*pageSize: count;
		int start =(page!=null && pageSize!=null)? end - pageSize:0;
		Map<String,Object> map=deskTopService.getNote_Notice(start,end);
		map.put("sessionId", request.getSession().getId());
		map.put("success",true);
	    return map;
	}
	/**
	 * 通知--新闻
	 * */
	@RequestMapping(value="/news/getNews.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getNews(HttpServletRequest request,Integer count,Integer page,Integer pageSize){
		int end = (page!=null && pageSize!=null)? page*pageSize: count;
		int start =(page!=null && pageSize!=null)? end - pageSize:0;
		Map<String,Object> map=deskTopService.getNews(start,end);
		map.put("success",true);
		map.put("sessionId", request.getSession().getId());
	    return map;
	}
	
	/**
	 * 订阅
	 * */
	@RequestMapping(value="/subs/getSubs.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getSubs(HttpServletRequest request,int count,String condition){
		Map<String,Object> map=deskTopService.getSubs(count,condition);
		map.put("success",true);
		map.put("sessionId", request.getSession().getId());
	    return map;
	}
	
	/**
	 * 客户生日提醒
	 * */
	@RequestMapping(value="/calls/getCustBirth.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getCustBirth(int count,String condition){
		Map<String,Object> map=deskTopService.getCustBirth(count,condition);
		map.put("success",true);
	    return map;
	}
	/**
	 * 系统问题反馈
	 * */
	@RequestMapping(value="/calls/getFeedback.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getFeedback(int count,String condition){
		Map<String,Object> map=deskTopService.getFeedback(count,condition);
		map.put("success",true);
	    return map;
	}
	
	/**
	 * kpi评估
	 * */
	@RequestMapping(value="/kpi/getKpibill.action", method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getKpibill(int count,String condition){
		Map<String,Object> map=deskTopService.getKpibill(count,condition);
		map.put("success",true);
	    return map;
	}
	
	/**
	 * 获取公共桌面设置
	 * */
	@RequestMapping(value="/getBench.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getBench(String portid) {
		Employee employee = SystemSession.getUser();
		Map<String,Object> modelMap = deskTopService.getBench(employee, portid);
		modelMap.put("success",true);
		return modelMap;
	}
	
	/**
	 * 获取公共桌面数据
	 * */
	@RequestMapping(value="/data.action", method = RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> getData(String caller, String condition, int pageSize) {
		Map<String,Object> modelMap = new HashMap<String, Object>();
		
		modelMap.put("data",deskTopService.getData(caller, condition, pageSize));
		modelMap.put("success",true);
		return modelMap;
	}
	
	/**
	 * 获取个人桌面设置
	 * */
	@RequestMapping(value="/getBenchSet.action", method = RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getBenchSet() {
		Employee employee = SystemSession.getUser();
		Map<String,Object> modelMap = new HashMap<String, Object>();
		modelMap.put("bench", deskTopService.getOwner(employee));
		modelMap.put("module", deskTopService.getBenchSet(employee));
		modelMap.put("success",true);
		return modelMap;
	}
	
	/**
	 * 报表查询
	 * */
	@RequestMapping(value="/newStyle/reports.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getReports(HttpSession session,String code){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data", deskTopService.getReports(employee,code));
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 查询汇总
	 * */
	@RequestMapping(value="/newStyle/querys.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getQuerys(HttpSession session,String code){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data", deskTopService.getQuerys(employee,code));
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 常用报表
	 * */
	@RequestMapping(value="/newStyle/myStore.action",method=RequestMethod.GET)
	@ResponseBody
	public Map<String,Object> getMyStore(HttpSession session){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		modelMap.put("data", deskTopService.getMyStore(employee));
		modelMap.put("success", true);
	    return modelMap;
	}
	
	/**
	 * 关注/取消关注 常用报表
	 * */
	@RequestMapping(value="/newStyle/changeReports.action",method=RequestMethod.POST)
	@ResponseBody
	public Map<String,Object> changeReports(HttpSession session,int sn_id,String type){
		Employee employee=(Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		deskTopService.changeReports(sn_id,employee,type);
		modelMap.put("success", true);
	    return modelMap;
	}
}

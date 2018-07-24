package com.uas.mobile.controller.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.core.support.SystemSession;
import com.uas.erp.model.Employee;
import com.uas.mobile.service.SubsService;

@Controller
public class SubsController {
	@Autowired
	private SubsService subsService;
	/**
	 * 获取订阅的实时看板
	 * */
	@RequestMapping(value="/mobile/getRealTimeSubs.action")
	@ResponseBody
	public Map<String,Object> getRealTimeSubs(HttpServletRequest request){
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		int emid = employee.getEm_id();
		Map<String,Object> map= new HashMap<String,Object>();
		map.put("subs", subsService.getRealTimeSubs(emid));
		map.put("success",true);
		map.put("sessionId", request.getSession().getId());
	    return map;
	}
	/**
	 * 获取实时看板图表
	 * */
	@RequestMapping(value="/mobile/mobileRealTimeCharts.action")
	@ResponseBody
	public ModelAndView mobileRealTimeCharts(HttpServletRequest req,HttpSession session,Integer numId){
		Map<String,Object> params=new HashMap<String,Object>();
		Employee employee=(Employee)session.getAttribute("employee");
		if(employee==null) BaseUtil.showError("会话已断开!");
		String sessionId = req.getParameter("sessionId");
		Map<Object,Object> store = subsService.mobileRealTimeCharts(numId, employee.getEm_id());
		params.put("numId",numId);
		params.put("mainId",store.get("mainId"));
		params.put("insId",store.get("insId"));
		params.put("title",store.get("title"));
		params.put("emId",employee.getEm_id());
		params.put("isMobile",sessionId!=null?1:0);
		return new ModelAndView("mobile/charts",params);
	}
	
	/**
	 * 获取订阅号参数配置及参数实例数据
	 * */
	@RequestMapping(value="/mobile/getSubsConditionsConfig.action")
	@ResponseBody
	public Map<String,Object> getSubsConditionsConfig(HttpServletRequest req,HttpSession session,Integer numId){
//		Employee employee = (Employee)session.getAttribute("employee");
		Employee employee = SystemSession.getUser();
		if(employee==null){
			BaseUtil.showError("会话已断开!");
		}
		return subsService.getSubsConditionsConfig(numId, employee.getEm_id());
	}
	
	/**
	 * 更新订阅号参数配置实例数据
	 * */
	@RequestMapping(value="/mobile/updateSubsConditionsInstance.action")
	@ResponseBody
	public ModelAndView updateSubsConditionsInstance(HttpServletRequest req,HttpSession session,Integer numId,String data){
		Map<String,Object> params=new HashMap<String,Object>();
//		Employee employee = (Employee)session.getAttribute("employee");
		Employee employee = SystemSession.getUser();
		if(employee==null){
			BaseUtil.showError("会话已断开!");
		}
		String sessionId = req.getParameter("sessionId");
		//更新参数实例数据
		subsService.updateSubsConditionsInstance(numId, employee.getEm_id(),data);
		
		Map<Object,Object> store = subsService.mobileRealTimeCharts(numId, employee.getEm_id());
		params.put("numId",numId);
		params.put("mainId",store.get("mainId"));
		params.put("insId",store.get("insId"));
		params.put("title",store.get("title"));
		params.put("emId",employee.getEm_id());
		params.put("isMobile",sessionId!=null?1:0);
		return new ModelAndView("mobile/charts",params);
	}
	
	/**
	 * 更新订阅号参数配置实例数据通过前台表单提交触发
	 * */
	@RequestMapping(value="/mobile/updateSubsConditionsInstanceByFormSubmit.action")
	@ResponseBody
	public Map<Object, Object> updateSubsConditionsInstanceByFormSubmit(HttpServletRequest req,HttpSession session,Integer numId,String data){
		Map<String,Object> params=new HashMap<String,Object>();
		Employee employee = SystemSession.getUser();
		if(employee==null){
			BaseUtil.showError("会话已断开!");
		}
		//更新参数实例数据
		subsService.updateSubsConditionsInstance(numId, employee.getEm_id(),data);
		
		Map<Object,Object> store = subsService.mobileRealTimeCharts(numId, employee.getEm_id());
		return store;
	}
	
	/**
	 * 获取关联关系配置
	 * @param numId
	 * @return
	 */
	@RequestMapping(value="mobile/getRefConfig.action")
	@ResponseBody
	public Map<String, Object> getRelConfig(int numId){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", subsService.getRelConfig(numId));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取关联下拉框的数据
	 * @param numId
	 * @param fieldName
	 * @param value
	 * @return
	 */
	@RequestMapping(value="mobile/getComboData.action")
	@ResponseBody
	public Map<String, Object> getComboData(int numId, String fieldName, String value){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("data", subsService.getComboData(fieldName, value, numId));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 点击链接，获取跳转新页面需要展示的信息
	 * @param subsNum
	 * @return
	 */
	@RequestMapping("mobile/getGridLinkedDate.action")
	@ResponseBody
	public Map<String, Object> getGridLinkedDate(String data, String formulaNum, String field){
		Map<String, Object> modelMap = subsService.getGridLinkedDate(data, formulaNum, field);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

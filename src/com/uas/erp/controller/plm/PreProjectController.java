package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.plm.PreProjectService;

@Controller
public class PreProjectController extends BaseController{
	
	@Autowired
	private PreProjectService preProjectService;
	
	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/plm/request/updatePreProject.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProjectService.updatePreProjectById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除预立项任务书数据 包括采购明细
	 */
	@RequestMapping("/plm/request/deletePreProject.action")
	@ResponseBody
	public Map<String, Object> deletePreProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProjectService.deletePreProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交预立项任务书
	 */
	@RequestMapping("/plm/request/submitPreProject.action")
	@ResponseBody
	public Map<String, Object> submitPreProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProjectService.submitPreProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交预立项任务书
	 */
	@RequestMapping("/plm/request/resSubmitPreProject.action")
	@ResponseBody
	public Map<String, Object> resSubmitPreProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProjectService.resSubmitPreProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核预立项任务书
	 */
	@RequestMapping("/plm/request/auditPreProject.action")
	@ResponseBody
	public Map<String, Object> auditPreProject(HttpSession session,String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		preProjectService.auditPreProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核预立项任务书
	 */
	@RequestMapping("/plm/request/resAuditPreProject.action")
	@ResponseBody
	public Map<String, Object> resAuditPreProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preProjectService.resAuditPreProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 预立项任务书转立项
	 */
	@RequestMapping("/plm/request/turnToProject.action")
	@ResponseBody
	public Map<String, Object> turnProject(HttpSession session,String caller, int id,String title) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		modelMap.put("msg",preProjectService.turnProject(id, caller,title,employee));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 获取明细行数据
	 */
	@RequestMapping("/plm/request/getID.action")
	@ResponseBody
	public Map<String, Object> getID(String formCondition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("Id",preProjectService.getID(formCondition));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 变更责任人
	 */
	@RequestMapping("/plm/request/changeResponsible.action")
	@ResponseBody
	public Map<String, Object> changeResponsible(HttpSession session,String caller,int id,String newman) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Employee employee = (Employee) session.getAttribute("employee");
		preProjectService.changeResponsible(caller,id,newman,employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

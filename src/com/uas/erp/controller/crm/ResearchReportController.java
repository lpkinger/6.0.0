package com.uas.erp.controller.crm;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.ResearchReportService;
@Controller
public class ResearchReportController {
	@Autowired
	private ResearchReportService researchReportService;
//	规范  小写
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("crm/marketmgr/saveResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.saveResearchReport(formStore,param, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/crm/marketmgr/deleteResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> deleteResearchReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.deleteResearchReport(id, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/crm/marketmgr/updateResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.updateResearchReportById(formStore,param, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/crm/marketmgr/submitResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> submitResearchReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.submitResearchReport(id, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/crm/marketmgr/resSubmitResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitResearchReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.resSubmitResearchReport(id, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> auditResearchReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.auditResearchReport(id, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditResearchReport.action")  
	@ResponseBody 
	public Map<String, Object> resAuditResearchReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		researchReportService.resAuditResearchReport(id, language, employee,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转差旅费报销
	 */
	@RequestMapping("/crm/marketmgr/turnFeepleaseCLFBX.action")  
	@ResponseBody 
	public Map<String, Object> turnFeepleaseCLFBX(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", researchReportService.turnFeepleaseCLFBX(id, language, employee, caller));
		return modelMap;
	}
}

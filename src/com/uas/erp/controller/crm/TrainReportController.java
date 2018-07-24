package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.TrainReportService;

@Controller
public class TrainReportController {
	@Autowired
	private TrainReportService trainReportService;
//	规范  小写
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/crm/customermgr/updateTrainReport.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainReportService.updateTrainReportById(formStore, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitTrainReport.action")  
	@ResponseBody 
	public Map<String, Object> submitTrainReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainReportService.submitTrainReport(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitTrainReport.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitTrainReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainReportService.resSubmitTrainReport(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditTrainReport.action")  
	@ResponseBody 
	public Map<String, Object> auditTrainReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainReportService.auditTrainReport(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditTrainReport.action")  
	@ResponseBody 
	public Map<String, Object> resAuditTrainReport(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainReportService.resAuditTrainReport(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

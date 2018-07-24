package com.uas.erp.controller.crm;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.MarketResearchService;
@Controller
public class MarketResearchController {
	@Autowired
	private  MarketResearchService marketResearchService;
//	规范  小写
	/**
	 * 保存form
	 * @param formStore form数据
	 */
	@RequestMapping("crm/customermgr/saveMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.saveMarketResearch(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/crm/customermgr/deleteMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> deleteMarketResearch(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.deleteMarketResearch(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form
	 * @param formStore form数据
	 */
	@RequestMapping("/crm/customermgr/updateMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.updateMarketResearchById(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/crm/customermgr/submitMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> submitMarketResearch(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.submitMarketResearch(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/crm/customermgr/resSubmitMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMarketResearch(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.resSubmitMarketResearch(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/crm/customermgr/auditMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> auditMarketResearch(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.auditMarketResearch(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/customermgr/resAuditMarketResearch.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMarketResearch(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		marketResearchService.resAuditMarketResearch(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
}

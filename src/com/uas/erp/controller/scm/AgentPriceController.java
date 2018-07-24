package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.AgentPriceService;

@Controller
public class AgentPriceController extends BaseController {
	@Autowired
	private AgentPriceService agentPriceService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.saveAgentPrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> deleteAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.deleteAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.updateAgentPriceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> printAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.printAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> submitAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.submitAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.resSubmitAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> auditAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.auditAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditAgentPrice.action")  
	@ResponseBody 
	public Map<String, Object> resAuditAgentPrice(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		agentPriceService.resAuditAgentPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

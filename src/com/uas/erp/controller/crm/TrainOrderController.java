package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.crm.TrainOrderService;

@Controller
public class TrainOrderController {
	@Autowired
	private TrainOrderService trainOrderService;
	/**
	 * 审核
	 */
	@RequestMapping("/crm/marketmgr/auditTrainOrder.action")  
	@ResponseBody 
	public Map<String, Object> auditTrainOrder(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainOrderService.auditTrainOrder(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/crm/marketmgr/resAuditTrainOrder.action")  
	@ResponseBody 
	public Map<String, Object> resAuditTrainOrder(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		trainOrderService.resAuditTrainOrder(id, language, employee, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.ManuFactFeeService;

@Controller
public class ManuFactFeeController {
	@Autowired
	private ManuFactFeeService manuFactFeeService;
	/**
	 * 保存ManuFactFee
	 * @param formStore form数据
	 * @param param 其它数据
	 */
	@RequestMapping("/co/cost/saveManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.saveManuFactFee(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改ManuFactFee
	 */
	@RequestMapping("/co/cost/updateManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.updateManuFactFee(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除ManuFactFee
	 */
	@RequestMapping("/co/cost/deleteManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.deleteManuFactFee(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核ManuFactFee
	 */
	@RequestMapping("/co/cost/auditManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.auditManuFactFee(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核ManuFactFee
	 */
	@RequestMapping("/co/cost/resAuditManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.resAuditManuFactFee(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交ManuFactFee
	 */
	@RequestMapping("/co/cost/submitManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.submitManuFactFee(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交ManuFactFee
	 */
	@RequestMapping("/co/cost/resSubmitManuFactFee.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		manuFactFeeService.resSubmitManuFactFee(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
}

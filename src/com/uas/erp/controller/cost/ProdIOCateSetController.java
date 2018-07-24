package com.uas.erp.controller.cost;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.cost.ProdIOCateSetService;

@Controller
public class ProdIOCateSetController extends BaseController {
	@Autowired
	private ProdIOCateSetService prodIOCateSetService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/co/cost/saveProdIOCateSet.action")  
	@ResponseBody 	
	public Map<String, Object> save(HttpSession session, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.saveProdIOCateSet(formStore, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}	
	@RequestMapping("/co/cost/updateProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.updateProdIOCateSetById(formStore,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/co/cost/deleteProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.deleteProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 审核ProdIOCateSet
	 */
	@RequestMapping("/co/cost/auditProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.auditProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核ProdIOCateSet
	 */
	@RequestMapping("/co/cost/resAuditProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.resAuditProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交ProdIOCateSet
	 */
	@RequestMapping("/co/cost/submitProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.submitProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交ProdIOCateSet
	 */
	@RequestMapping("/co/cost/resSubmitProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.resSubmitProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/co/cost/bannedProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> banned(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.bannedProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/co/cost/resBannedProdIOCateSet.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodIOCateSetService.resBannedProdIOCateSet(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
}

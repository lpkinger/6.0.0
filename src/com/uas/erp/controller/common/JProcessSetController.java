package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseUtil;
import com.uas.erp.model.Employee;
import com.uas.erp.service.common.JProcessSetService;
@Controller
public class JProcessSetController {
	@Autowired
	JProcessSetService jProcessSetServive;
	@RequestMapping(value = "/common/getFormDataByformCondition.action")
	@ResponseBody
	public Map<String,Object> getFormDataByformCondition(HttpServletRequest request,String formCondition){		
		Employee employee = (Employee) request.getSession().getAttribute(
				"employee");
		if (employee == null)
			BaseUtil.showError("会话已断开!");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("datas",jProcessSetServive.getFormDataByformCondition(formCondition));
		modelMap.put("sessionId", request.getSession().getId());
		return modelMap;
	}

	@RequestMapping("/common/saveJprocessSet.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String caller, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.saveJProcessSet(caller, formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteJprocessSet.action")  
	@ResponseBody 
	public Map<String, Object> deleteJProcessSet(HttpSession session, String caller, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.deleteJProcessSet(caller, id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改
	 */
	@RequestMapping("/common/updateJprocessSet.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String caller, String formStore, String param) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.updateJProcessSetById(caller, formStore, param, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 流程模板设置
	 * */
	@RequestMapping("/common/saveJprocessTemplate.action")
	@ResponseBody
	public Map<String,Object> saveJprocessTemplate(HttpSession session,String formStore,String clobtext){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.saveJprocessTemplate(formStore,clobtext,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 流程模板更新
	 * */
	@RequestMapping("/common/updateJprocessTemplate.action")
	@ResponseBody
	public Map<String,Object> updateJprocessTemplate(HttpSession session,String formStore,String clobtext){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.updateJprocessTemplate(formStore,clobtext,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteJprocessTemplate.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.deleteJprocessTemplate(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/common/auditJprocessTemplate.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.auditJprocessTemplate(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/common/resAuditJprocessTemplate.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.resAuditJprocessTemplate(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/common/submitJprocessTemplate.action")  
	@ResponseBody 
	public Map<String, Object> submit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.submitJprocessTemplate(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/common/resSubmitJprocessTemplate.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.resSubmitJprocessTemplate(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 自定义流程保存
	 * */
	@RequestMapping("/common/saveAutoJprocess.action")
	@ResponseBody
	public Map<String,Object> saveAutoJprocess(HttpSession session,String formStore,String clobtext){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.saveAutoJprocess(formStore,clobtext,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 流程模板更新
	 * */
	@RequestMapping("/common/updateAutoJprocess.action")
	@ResponseBody
	public Map<String,Object> updateAutoJprocess(HttpSession session,String formStore,String clobtext){
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.updateAutoJprocess(formStore,clobtext,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/common/deleteAutoJprocess.action")  
	@ResponseBody 
	public Map<String, Object> deleteAutoJprocess(HttpSession session, int id) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.deleteAutoJprocess(id, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/common/auditAutoJprocess.action")  
	@ResponseBody 
	public Map<String, Object> auditAutoJprocess(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.auditAutoJprocess(id, caller,language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/common/resAuditAutoJprocess.action")  
	@ResponseBody 
	public Map<String, Object> resAuditAutoJprocess(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.resAuditAutoJprocess(id,caller, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/common/submitAutoJprocess.action")  
	@ResponseBody 
	public Map<String, Object> submitAutoJprocess(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.submitAutoJprocess(id,caller, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/common/resSubmitAutoJprocess.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitAutoJprocess(HttpSession session, int id,String caller) {
		String language = (String)session.getAttribute("language");
		Employee employee = (Employee)session.getAttribute("employee");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		jProcessSetServive.resSubmitAutoJprocess(id,caller, language, employee);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 对象选择时筛选
	 * */
	@RequestMapping("/common/ProcessQueryPersons.action")  
	@ResponseBody 
	public Map<String, Object> ProcessQueryPersons(HttpSession session,String likestring) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    modelMap.put("data",jProcessSetServive.ProcessQueryPersons(likestring));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 代理商对象选择时筛选
	 * */
	@RequestMapping("/common/ProcessQueryAgentPersons.action")  
	@ResponseBody 
	public Map<String, Object> ProcessQueryAgentPersons(HttpSession session,String likestring) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    modelMap.put("data",jProcessSetServive.ProcessQueryAgentPersons(likestring));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 *获取流程设置 
	 * */
	@RequestMapping("/common/getJprocessSet.action")
	@ResponseBody
	public Map<String,Object> getJprocessSet(HttpSession session,String caller){
		Map<String,Object> modelMap=new HashMap<String,Object>();
		modelMap.put("data",jProcessSetServive.getJprocessSet(caller));
		modelMap.put("success",true);
		return modelMap;
	}
}

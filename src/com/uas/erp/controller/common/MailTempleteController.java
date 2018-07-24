package com.uas.erp.controller.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.MailTempleteService;

@Controller
public class MailTempleteController {
	
	@Autowired
	private MailTempleteService mailTempleteService;

	/**
	 * 保存邮件模板
	 * @param caller
	 * @param formStore
	 * @param param
	 * @return
	 */
	@RequestMapping("/common/mailTemp/saveMailTemp.action")
	@ResponseBody
	public Map<String, Object> saveMailTemp(String caller, String formStore, String param){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.saveMailTemplete(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除邮件模板
	 * @param caller
	 * @param id
	 * @return
	 */
	@RequestMapping("/common/mailTemp/deleteMailTemp.action")
	@ResponseBody
	public Map<String, Object> deleteMailTemp(String caller, int id){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.deleteMailTempleteById(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改邮件模板
	 * @param formStore
	 *            form数据
	 */
	@RequestMapping("/common/mailTemp/updateMailTemp.action")
	@ResponseBody
	public Map<String, Object> updateMailTemp(String caller, String formStore) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.updateMailTemplete(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交邮件模板
	 */
	@RequestMapping("/common/mailTemp/submitMailTemp.action")
	@ResponseBody
	public Map<String, Object> submitMailTemp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.submitMailTemplete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反提交邮件模板
	 */
	@RequestMapping("/common/mailTemp/resSubmitMailTemp.action")
	@ResponseBody
	public Map<String, Object> resSubmitMailTemp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.resSubmitMailTemplete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 审核邮件模板
	 */
	@RequestMapping("/common/mailTemp/auditMailTemp.action")
	@ResponseBody
	public Map<String, Object> auditMailTemp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.auditMailTemplete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 反审核邮件模板
	 */
	@RequestMapping("/common/mailTemp/resAuditMailTemp.action")
	@ResponseBody
	public Map<String, Object> resAuditMailTemp(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mailTempleteService.resAuditMailTemplete(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

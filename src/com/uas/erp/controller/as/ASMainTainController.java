package com.uas.erp.controller.as;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.as.MainTainService;

@Controller
public class ASMainTainController {
     
	@Autowired
	private MainTainService mainTainService;
	/**
	 * 保存
	 */
	@RequestMapping("/as/port/saveMainTain.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.saveMainTain(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/as/port/deleteMainTain.action")  
	@ResponseBody 
	public Map<String, Object> deleteLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.deleteMainTain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/as/port/updateMainTain.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		mainTainService.updateMainTain(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/as/port/submitMainTain.action")  
	@ResponseBody 
	public Map<String, Object> submitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.submitMainTain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/as/port/resSubmitMainTain.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.resSubmitMainTain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 售后确认
	 */
	@RequestMapping("/as/port/marketMainTain.action")  
	@ResponseBody 
	public Map<String, Object> marketApply(int id,String value,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		/*String value="RETURNING";*/
		mainTainService.marketMainTain(id,value, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/as/port/auditMainTain.action")  
	@ResponseBody 
	public Map<String, Object> auditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.auditMainTain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/as/port/resAuditMainTain.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mainTainService.resAuditMainTain(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.Employee;
import com.uas.erp.service.scm.CustTurnService;

@Controller
public class CustTurnController extends BaseController {
     
	@Autowired
	private CustTurnService custTurnService;
	/**
	 * 保存
	 */
	@RequestMapping("/scm/sale/saveCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.saveCustTurn(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> deleteLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.deleteCustTurn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/scm/sale/updateCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();		
		custTurnService.updateCustTurn(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> submitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.submitCustTurn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.resSubmitCustTurn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> auditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.auditCustTurn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditCustTurn.action")  
	@ResponseBody 
	public Map<String, Object> resAuditLineApply(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		custTurnService.resAuditCustTurn(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

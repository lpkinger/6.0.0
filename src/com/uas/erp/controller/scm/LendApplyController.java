package com.uas.erp.controller.scm;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.LendApplyService;

@Controller
public class LendApplyController extends BaseController {
	@Autowired
	private LendApplyService lendApplyService;
	/**
	 * 保存
	 */
	@RequestMapping("/scm/sale/saveLendApply.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller,String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.saveLendApply(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 更改
	 */
	@RequestMapping("/scm/sale/updateLendApply.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller,String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.updateLendApplyById(caller,formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}	
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteLendApply.action")  
	@ResponseBody 
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.deleteLendApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交操作
	 */
	@RequestMapping("/scm/sale/submitLendApply.action")
	@ResponseBody
	public Map<String, Object> submitLendApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.submitLendApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交操作
	 */
	@RequestMapping("/scm/sale/resSubmitLendApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitLendApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.resSubmitLendApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditLendApply.action")  
	@ResponseBody 
	public Map<String, Object> auditLendApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.auditLendApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核操作
	 */
	@RequestMapping("/scm/sale/resAuditLendApply.action")
	@ResponseBody
	public Map<String, Object> resAuditLendApply(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		lendApplyService.resAuditLendApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 生成借调申请单
	 */
	@RequestMapping("/scm/sale/addLendApply.action")
	@ResponseBody
	public Map<String, Object> addLendApply(String formdata, String data ,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
	    String log = lendApplyService.addLendApply(formdata, data,caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}
}


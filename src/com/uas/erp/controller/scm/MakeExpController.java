package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.MakeExpService;

@Controller
public class MakeExpController extends BaseController {
	@Autowired
	private MakeExpService makeExpService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/saveMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.saveMakeExp(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> deleteMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.deleteMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/purchase/updateMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.updateMakeExpById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> printMake(String caller, int id,String reportName,String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys=makeExpService.printMakeExp(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> submitMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.submitMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.resSubmitMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> auditMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.auditMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMake(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.resAuditMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/purchase/bannedMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.bannedMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/purchase/resBannedMakeExp.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.resBannedMakeExp(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}//
	/**
	 * 获取定价
	 */
	@RequestMapping("/scm/purchase/getPOPrice.action")  
	@ResponseBody 
	public Map<String, Object> getPOPrice(String me_code) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		makeExpService.getPOPrice(me_code);
		modelMap.put("success", true);
		return modelMap;
	}
}

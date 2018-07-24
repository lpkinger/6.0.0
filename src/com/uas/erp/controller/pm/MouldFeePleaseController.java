package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MouldFeePleaseService;

@Controller
public class MouldFeePleaseController extends BaseController {
	@Autowired
	private MouldFeePleaseService mouldFeePleaseService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/saveMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.saveMouldFeePlease(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/pm/mould/deleteMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> deleteMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.deleteMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/updateMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.updateMouldFeePleaseById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> print(String caller, int id,
			String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = mouldFeePleaseService.printMouldFeePlease(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> submitMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.submitMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.resSubmitMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> auditMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.auditMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.resAuditMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转银行登记
	 */
	@RequestMapping("/pm/mould/turnAccountRegister.action")  
	@ResponseBody 
	public Map<String, Object> turnAccountRegister(int id, String thisamount, String catecode, String thisdate, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", mouldFeePleaseService.turnAccountRegister(id, thisamount, catecode, thisdate, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转应付票据
	 */
	@RequestMapping("/pm/mould/turnBillAP.action")  
	@ResponseBody 
	public Map<String, Object> turnBillAP(int id, String thisamount, String catecode, String thisdate, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", mouldFeePleaseService.turnBillAP(id, thisamount, catecode, thisdate, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转应收票据异动
	 */
	@RequestMapping("/pm/mould/turnBillARChange.action")  
	@ResponseBody 
	public Map<String, Object> turnBillARChange(int id, String thisamount, String catecode, String thisdate, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", mouldFeePleaseService.turnBillARChange(id, thisamount, catecode, thisdate, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 结案
	 */
	@RequestMapping("/pm/mould/endMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> endMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.endMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案
	 */
	@RequestMapping("/pm/mould/resEndMouldFeePlease.action")  
	@ResponseBody 
	public Map<String, Object> resEndMouldFeePlease(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldFeePleaseService.resEndMouldFeePlease(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}


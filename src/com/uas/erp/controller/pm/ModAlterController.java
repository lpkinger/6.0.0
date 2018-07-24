package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.model.FileUpload;
import com.uas.erp.service.pm.ModAlterService;

@Controller
public class ModAlterController extends BaseController {
	@Autowired
	private ModAlterService modAlterService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveModAlter.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.saveModAlter(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteModAlter.action")
	@ResponseBody
	public Map<String, Object> deleteModAlter(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.deleteModAlter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/updateModAlter.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.updateModAlterById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printModAlter.action")
	@ResponseBody
	public Map<String, Object> printModAlter(String caller,
			int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = modAlterService.printModAlter(id, caller,
				reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitModAlter.action")
	@ResponseBody
	public Map<String, Object> submitModAlter(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.submitModAlter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitModAlter.action")
	@ResponseBody
	public Map<String, Object> resSubmitModAlter(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.resSubmitModAlter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditModAlter.action")
	@ResponseBody
	public Map<String, Object> auditModAlter(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.auditModAlter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditModAlter.action")
	@ResponseBody
	public Map<String, Object> resAuditModAlter(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.resAuditModAlter(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具付款申请单
	 */
	@RequestMapping("/pm/mould/turnFeePlease.action")
	@ResponseBody
	public Map<String, Object> turnFeePlease(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", modAlterService.turnFeePlease(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转报价单
	 */
	@RequestMapping("/pm/mould/alterTurnPriceMould.action")  
	@ResponseBody 
	public Map<String, Object> turnPriceMould(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", modAlterService.turnPriceMould(id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转模具销售单
	 */
	@RequestMapping("/pm/mould/alterTurnMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> turnMouldSale(HttpSession session, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", modAlterService.turnMouldSale(id));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 上传明细的附件
	 */
	@RequestMapping("/pm/mould/uploadDetailFile.action")  
	@ResponseBody 
	public Map<String, Object> uploadDetailFile(HttpSession session, String params,String caller,String code,String keyvalue,String keyField) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.uploadDetailFile(params,caller,code,keyvalue,keyField);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除明细的附件
	 */
	@RequestMapping("/pm/mould/deleteDetailFile.action")  
	@ResponseBody 
	public Map<String, Object> deleteDetailFile(HttpSession session, Integer id,String caller,String code,String keyvalue,String keyField) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modAlterService.deleteDetailFile(id,caller,code,keyvalue,keyField);
		modelMap.put("success", true);
		return modelMap;
	}
}

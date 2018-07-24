package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.InquiryMouldService;

@Controller
public class InquiryMouldController extends BaseController {
	@Autowired
	private InquiryMouldService inquiryMouldService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveInquiryMould.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.saveInquiryMould(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteInquiryMould.action")
	@ResponseBody
	public Map<String, Object> deleteInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.deleteInquiryMould(id, caller);
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
	@RequestMapping("/pm/mould/updateInquiryMould.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.updateInquiryMouldById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printInquiryMould.action")
	@ResponseBody
	public Map<String, Object> printInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.printInquiryMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitInquiryMould.action")
	@ResponseBody
	public Map<String, Object> submitInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.submitInquiryMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitInquiryMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.resSubmitInquiryMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditInquiryMould.action")
	@ResponseBody
	public Map<String, Object> auditInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.auditInquiryMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditInquiryMould.action")
	@ResponseBody
	public Map<String, Object> resAuditInquiryMould(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.resAuditInquiryMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转物料核价单
	 */
	@RequestMapping("/pm/mould/turnPurcPrice.action")
	@ResponseBody
	public Map<String, Object> turnPurcPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		id = inquiryMouldService.turnPurcPrice(id, caller);
		modelMap.put("id", id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 作废
	 */
	@RequestMapping("/pm/mould/nullifyInquiryMould.action")
	@ResponseBody
	public Map<String, Object> nullifyInquiryMould(int id, String caller, String reason) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.nullifyInquiryMould(id, caller, reason);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 作废之前的检测
	 */
	@RequestMapping("/pm/mould/nullifybeforeCheck.action")
	@ResponseBody
	public Map<String, Object> nullifybeforeCheck(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		inquiryMouldService.nullifybeforeCheck(id);
		modelMap.put("success", true);
		return modelMap;
	}

}

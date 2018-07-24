package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.ProcessPriceService;

@Controller
public class ProcessPriceController {
	@Autowired
	private ProcessPriceService processPriceService;
	
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveProcessPrice.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.saveProcessPrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteProcessPrice.action")
	@ResponseBody
	public Map<String, Object> deleteProcessPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.deleteProcessPrice(id, caller);
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
	@RequestMapping("/pm/mould/updateProcessPrice.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.updateProcessPriceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printProcessPrice.action")
	@ResponseBody
	public Map<String, Object> printProcessPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.printProcessPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitProcessPrice.action")
	@ResponseBody
	public Map<String, Object> submitProcessPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.submitProcessPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitProcessPrice.action")
	@ResponseBody
	public Map<String, Object> resSubmitProcessPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.resSubmitProcessPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditProcessPrice.action")
	@ResponseBody
	public Map<String, Object> auditProcessPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.auditProcessPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditProcessPrice.action")
	@ResponseBody
	public Map<String, Object> resAuditProcessPrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		processPriceService.resAuditProcessPrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

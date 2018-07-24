package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.pm.MouldProdHourService;

@Controller
public class MouldProdHourController {
	
	
	@Autowired
	private MouldProdHourService mouldProdHourService;
	
	
	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/saveMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.saveMouldProdHour(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deleteMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> deleteMouldProdHour(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.deleteMouldProdHour(id, caller);
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
	@RequestMapping("/pm/mould/updateMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.updateMouldProdHourById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> printMouldProdHour(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.printMouldProdHour(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> submitMouldProdHour(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.submitMouldProdHour(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> resSubmitMouldProdHour(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.resSubmitMouldProdHour(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> auditMouldProdHour(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.auditMouldProdHour(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditMouldProdHour.action")
	@ResponseBody
	public Map<String, Object> resAuditMouldProdHour(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldProdHourService.resAuditMouldProdHour(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProdInOutOthService;

@Controller
public class ProdInOutOthController extends BaseController {
	@Autowired
	private ProdInOutOthService prodInOutOthService;

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mes/deleteProdInOut.action")
	@ResponseBody
	public Map<String, Object> deleteProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.deleteProdInOut(id, caller);
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
	@RequestMapping("/pm/mes/updateProdInOut.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.updateProdInOutById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mes/printProdInOut.action")
	@ResponseBody
	public Map<String, Object> printProdInOut(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.printProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitProdInOut.action")
	@ResponseBody
	public Map<String, Object> submitProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.submitProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitProdInOut.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.resSubmitProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditProdInOut.action")
	@ResponseBody
	public Map<String, Object> auditProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.auditProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditProdInOut.action")
	@ResponseBody
	public Map<String, Object> resAuditProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.resAuditProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 过账
	 */
	@RequestMapping("/pm/mes/postProdInOut.action")
	@ResponseBody
	public Map<String, Object> postProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.postProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反过账
	 */
	@RequestMapping("/pm/mes/resPostProdInOut.action")
	@ResponseBody
	public Map<String, Object> resPosttProdInOut(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.resPostProdInOut(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 确定冲减
	 */
	@RequestMapping("/pm/mes/saveProdIOClash.action")
	@ResponseBody
	public Map<String, Object> saveProdIOClash(String caller, String data, int id, double clashqty) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.saveProdIOClash(caller, data, id, clashqty);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 设置冲减
	 */
	@RequestMapping("/pm/mes/setProdIOClash.action")
	@ResponseBody
	public Map<String, Object> setProdIOClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodInOutOthService.setProdIOClash(id,caller);
		modelMap.put("success", true);
		return modelMap;
	}
	@RequestMapping("/pm/mes/getClashInfo.action")
	@ResponseBody
	public Map<String, Object> getClashInfo(String caller, String con) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("info", prodInOutOthService.getClashInfo(caller, con));
		modelMap.put("success", true);
		return modelMap;
	}

}

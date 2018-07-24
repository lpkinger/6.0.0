package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.PriceMouldService;

@Controller
public class PriceMouldController extends BaseController {
	@Autowired
	private PriceMouldService priceMouldService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mould/savePriceMould.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.savePriceMould(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mould/deletePriceMould.action")
	@ResponseBody
	public Map<String, Object> deletePriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.deletePriceMould(id, caller);
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
	@RequestMapping("/pm/mould/updatePriceMould.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.updatePriceMouldById(formStore, param, param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printPriceMould.action")
	@ResponseBody
	public Map<String, Object> printPriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.printPriceMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitPriceMould.action")
	@ResponseBody
	public Map<String, Object> submitPriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.submitPriceMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitPriceMould.action")
	@ResponseBody
	public Map<String, Object> resSubmitPriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.resSubmitPriceMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditPriceMould.action")
	@ResponseBody
	public Map<String, Object> auditPriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.auditPriceMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditPriceMould.action")
	@ResponseBody
	public Map<String, Object> resAuditPriceMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		priceMouldService.resAuditPriceMould(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转模具询价单
	 */
	@RequestMapping("/pm/mould/turnInquiry.action")
	@ResponseBody
	public Map<String, Object> turnmould(String caller, int id) {
		return success(priceMouldService.turnInquiry(id, caller));
	}

	/**
	 * 转模具采购单
	 */
	@RequestMapping("/pm/mould/turnPurcMould.action")
	@ResponseBody
	public Map<String, Object> turnPurMould(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("id", priceMouldService.turnPurMould(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
}

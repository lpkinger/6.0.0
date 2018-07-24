package com.uas.erp.controller.drp;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;

import com.uas.erp.service.drp.SalePriceApplyService;

@Controller
public class SalePriceApplyController extends BaseController {
	@Autowired
	private SalePriceApplyService salePriceApplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/drp/distribution/saveSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.saveSalePriceApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/drp/distribution/deleteSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> deleteSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.deleteSalePriceApply(id, caller);
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
	@RequestMapping("/drp/distribution/updateSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService
				.updateSalePriceApplyById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/drp/distribution/printSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> printSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.printSalePriceApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> submitSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.submitSalePriceApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.resSubmitSalePriceApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> auditSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.auditSalePriceApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditSalePriceApply.action")
	@ResponseBody
	public Map<String, Object> resAuditSalePriceApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceApplyService.resAuditSalePriceApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

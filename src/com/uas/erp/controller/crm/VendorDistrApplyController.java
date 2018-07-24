package com.uas.erp.controller.crm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.crm.VendorDistrApplyService;

@Controller
public class VendorDistrApplyController {
	@Autowired
	private VendorDistrApplyService vendorDistrApplyService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/crm/chance/saveVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.saveVendorDistrApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/crm/chance/deleteVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> deleteVendorDistrApply(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.deleteVendorDistrApply(id, caller);
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
	@RequestMapping("/crm/chance/updateVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService
				.updateVendorDistrApply(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/Chance/submitVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> submitChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.submitVendorDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/Chance/resSubmitVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> resSubmitChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.resSubmitVendorDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/Chance/auditVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> auditChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.auditVendorDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/Chance/resAuditVendorDistrApply.action")
	@ResponseBody
	public Map<String, Object> resAuditChance(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorDistrApplyService.resAuditVendorDistrApply(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

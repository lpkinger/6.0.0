package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProductSMTService;

@Controller
public class ProductSMTController extends BaseController {
	@Autowired
	private ProductSMTService productSMTService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/mes/saveProductSMT.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.saveProductSMT(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/pm/mes/deleteProductSMT.action")
	@ResponseBody
	public Map<String, Object> deleteProductSMT(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.deleteProductSMT(id, caller);
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
	@RequestMapping("/pm/mes/updateProductSMT.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.updateProductSMTById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/pm/mes/printProductSMT.action")
	@ResponseBody
	public Map<String, Object> printProductSMT(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.printProductSMT(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/mes/submitProductSMT.action")
	@ResponseBody
	public Map<String, Object> submitProductSMT(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.submitProductSMT(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mes/resSubmitProductSMT.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductSMT(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.resSubmitProductSMT(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/mes/auditProductSMT.action")
	@ResponseBody
	public Map<String, Object> auditProductSMT(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.auditProductSMT(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mes/resAuditProductSMT.action")
	@ResponseBody
	public Map<String, Object> resAuditProductSMT(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productSMTService.resAuditProductSMT(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

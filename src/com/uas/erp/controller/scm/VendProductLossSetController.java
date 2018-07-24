package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VendProductLossSetService;

@Controller
public class VendProductLossSetController extends BaseController {
	@Autowired
	private VendProductLossSetService productLossSetService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/product/saveVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.saveVendProductLossSet(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/product/deleteVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> deleteProductLossSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.deleteVendProductLossSet(id, caller);
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
	@RequestMapping("/scm/product/updateVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.updateVendProductLossSetById(formStore, param,caller);
		modelMap.put("success", true);
		return modelMap;
	}


	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> submitProductLossSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.submitVendProductLossSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductLossSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.resSubmitVendProductLossSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/product/auditVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> auditProductLossSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String log = productLossSetService.auditVendProductLossSet(id, caller);
		modelMap.put("success", true);
		modelMap.put("log", log);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditVendProductLossSet.action")
	@ResponseBody
	public Map<String, Object> resAuditProductLossSet(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productLossSetService.resAuditVendProductLossSet(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

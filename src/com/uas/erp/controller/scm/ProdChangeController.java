package com.uas.erp.controller.scm;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ProdChangeService;

@Controller
public class ProdChangeController extends BaseController {
	@Autowired
	private ProdChangeService prodChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/product/saveProdChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.saveProdChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/product/deleteProdChange.action")
	@ResponseBody
	public Map<String, Object> deleteProdChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.deleteProdChange(id, caller);
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
	@RequestMapping("/scm/product/updateProdChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.updateProdChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/product/printProdChange.action")
	@ResponseBody
	public Map<String, Object> printProdChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.printProdChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProdChange.action")
	@ResponseBody
	public Map<String, Object> submitProdChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.submitProdChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProdChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.resSubmitProdChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * 
	 * @throws UnknownHostException
	 */
	@RequestMapping("/scm/product/auditProdChange.action")
	@ResponseBody
	public ModelMap auditProdChange(String caller, int id) {
		prodChangeService.auditProdChange(id, caller);
		return success();
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProdChange.action")
	@ResponseBody
	public Map<String, Object> resAuditProdChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodChangeService.resAuditProdChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

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
import com.uas.erp.service.scm.ProdKindChangeService;

@Controller
public class ProdKindChangeController extends BaseController {
	@Autowired
	private ProdKindChangeService prodKindChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/product/saveProdKindChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.saveProdKindChange(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/product/deleteProdKindChange.action")
	@ResponseBody
	public Map<String, Object> deleteProdKindChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.deleteProdKindChange(id, caller);
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
	@RequestMapping("/scm/product/updateProdKindChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.updateProdKindChangeById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/product/printProdKindChange.action")
	@ResponseBody
	public Map<String, Object> printProdKindChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.printProdKindChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/product/submitProdKindChange.action")
	@ResponseBody
	public Map<String, Object> submitProdKindChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.submitProdKindChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/product/resSubmitProdKindChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitProdKindChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.resSubmitProdKindChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 * 
	 * @throws UnknownHostException
	 */
	@RequestMapping("/scm/product/auditProdKindChange.action")
	@ResponseBody
	public ModelMap auditProdKindChange(String caller, int id) {
		prodKindChangeService.auditProdKindChange(id, caller);
		return success();
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/product/resAuditProdKindChange.action")
	@ResponseBody
	public Map<String, Object> resAuditProdKindChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		prodKindChangeService.resAuditProdKindChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

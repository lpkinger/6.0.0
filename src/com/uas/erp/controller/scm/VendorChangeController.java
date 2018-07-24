package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VendorChangeService;

@Controller
public class VendorChangeController extends BaseController {
	@Autowired
	private VendorChangeService VendorChangeService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveVendorChange.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.saveVendorChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteVendorChange.action")
	@ResponseBody
	public Map<String, Object> deleteVendorChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.deleteVendorChange(caller, id);
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
	@RequestMapping("/scm/purchase/updateVendorChange.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.updateVendorChangeById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVendorChange.action")
	@ResponseBody
	public Map<String, Object> submitVendorChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.submitVendorChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVendorChange.action")
	@ResponseBody
	public Map<String, Object> resSubmitVendorChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.resSubmitVendorChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVendorChange.action")
	@ResponseBody
	public Map<String, Object> auditVendorChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.auditVendorChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditVendorChange.action")
	@ResponseBody
	public Map<String, Object> resAuditVendorChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		VendorChangeService.resAuditVendorChange(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 打印
	 */
	@RequestMapping("/scm/purchase/printVendorChange.action")
	@ResponseBody
	public Map<String, Object> print(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = VendorChangeService.printVendorChange(caller, id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
}

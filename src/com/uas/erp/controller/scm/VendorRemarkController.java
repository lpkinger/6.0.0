package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.VendorRemarkService;

@Controller
public class VendorRemarkController extends BaseController {
	@Autowired
	private VendorRemarkService vendorRemarkService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/scm/purchase/saveVendorRemark.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorRemarkService.saveVendorRemark(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除 包括明细
	 */
	@RequestMapping("/scm/purchase/deleteVendorRemark.action")
	@ResponseBody
	public Map<String, Object> deleteVendorRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorRemarkService.deleteVendorRemark(id, caller);
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
	@RequestMapping("/scm/purchase/updateVendorRemark.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorRemarkService.updateVendorRemarkById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/purchase/bannedVendorRemark.action")
	@ResponseBody
	public Map<String, Object> bannedVendorRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorRemarkService.bannedVendorRemark(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/purchase/resBannedVendorRemark.action")
	@ResponseBody
	public Map<String, Object> resBannedVendorRemark(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorRemarkService.resBannedVendorRemark(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

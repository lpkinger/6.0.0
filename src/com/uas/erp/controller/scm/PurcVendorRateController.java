package com.uas.erp.controller.scm;

import com.uas.erp.service.scm.PurcVendorRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PurcVendorRateController {

	@Autowired
	private PurcVendorRateService purcVendorRateService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("scm/purchase/savePurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.savePurcVendorRate(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("scm/purchase/updatePurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.updatePurcVendorRateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("scm/purchase/deletePurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.deletePurcVendorRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("scm/purchase/submitPurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> submitPurcVendorRate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.submitPurcVendorRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */             
	@RequestMapping("scm/purchase/resSubmitPurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> resSubmitPurcVendorRate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.resSubmitPurcVendorRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("scm/purchase/auditPurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> auditPurcVendorRate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.auditPurcVendorRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("scm/purchase/resAuditPurcVendorRate.action")
	@ResponseBody
	public Map<String, Object> resAuditPurcVendorRate(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		purcVendorRateService.resAuditPurcVendorRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

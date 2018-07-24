package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.common.CommonService;
import com.uas.erp.service.scm.VendorClaimService;
import com.uas.erp.service.scm.VendorPerformanceAssessService;

/**
 * 供应商索赔的controller 对应前台通用的jsp:jsps/scm/purchase/vendorClaim.jsp
 */
@Controller
public class VendorClaimController {
	
	@Autowired
	private VendorClaimService vendorClaimService;

	/**
	 * 保存
	 */
	@RequestMapping("/scm/purchase/saveVendorClaim.action")
	@ResponseBody
	public Map<String, Object> saveVendorClaim(String caller,  String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.saveVendorClaim(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/scm/purchase/deleteVendorClaim.action")
	@ResponseBody
	public Map<String, Object> deleteVendorClaim(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.deleteVendorClaim(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/scm/purchase/updateVendorClaim.action")
	@ResponseBody
	public Map<String, Object> updateVendorClaim(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.updateVendorClaim(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/scm/purchase/submitVendorClaim.action")
	@ResponseBody
	public Map<String, Object> submitVendorClaim(String caller,  int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.submitVendorClaim(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/scm/purchase/resSubmitVendorClaim.action")
	@ResponseBody
	public Map<String, Object> resSubmitVendorClaim(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.resSubmitVendorClaim(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/scm/purchase/auditVendorClaim.action")
	@ResponseBody
	public Map<String, Object> auditVendorClaim(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",vendorClaimService.auditVendorClaim(id,caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/scm/purchase/resAuditVendorClaim.action")
	@ResponseBody
	public Map<String, Object> resAuditVendorClaim(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		vendorClaimService.resAuditVendorClaim(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 转其它应付单
	 */
	@RequestMapping("/scm/purchase/turnAPBillVendorClaim.action")
	@ResponseBody
	public Map<String, Object> turnAPBillVendorClaim(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",vendorClaimService.turnAPBillVendorClaim(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}
}

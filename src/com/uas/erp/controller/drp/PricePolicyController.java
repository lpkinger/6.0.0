package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.PricePolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PricePolicyController {

	@Autowired
	private PricePolicyService pricePolicyService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/savePricePolicy.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.savePricePolicy(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updatePricePolicy.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.updatePricePolicyById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deletePricePolicy.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.deletePricePolicy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitPricePolicy.action")
	@ResponseBody
	public Map<String, Object> submitPricePolicy(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.submitPricePolicy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitPricePolicy.action")
	@ResponseBody
	public Map<String, Object> resSubmitPricePolicy(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.resSubmitPricePolicy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditPricePolicy.action")
	@ResponseBody
	public Map<String, Object> auditPricePolicy(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.auditPricePolicy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditPricePolicy.action")
	@ResponseBody
	public Map<String, Object> resAuditPricePolicy(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		pricePolicyService.resAuditPricePolicy(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

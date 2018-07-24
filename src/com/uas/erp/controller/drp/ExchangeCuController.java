package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.ExchangeCuService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class ExchangeCuController {

	@Autowired
	private ExchangeCuService exchangeCuService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/saveExchangeCu.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.saveExchangeCu(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updateExchangeCu.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.updateExchangeCuById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deleteExchangeCu.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.deleteExchangeCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitExchangeCu.action")
	@ResponseBody
	public Map<String, Object> submitExchangeCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.submitExchangeCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitExchangeCu.action")
	@ResponseBody
	public Map<String, Object> resSubmitExchangeCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.resSubmitExchangeCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditExchangeCu.action")
	@ResponseBody
	public Map<String, Object> auditExchangeCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.auditExchangeCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditExchangeCu.action")
	@ResponseBody
	public Map<String, Object> resAuditExchangeCu(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		exchangeCuService.resAuditExchangeCu(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

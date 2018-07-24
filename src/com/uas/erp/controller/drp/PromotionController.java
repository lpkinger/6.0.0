package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.PromotionService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class PromotionController {

	@Autowired
	private PromotionService promotionService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/distribution/savePromotion.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.savePromotion(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/distribution/updatePromotion.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.updatePromotionById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/distribution/deletePromotion.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.deletePromotion(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/distribution/submitPromotion.action")
	@ResponseBody
	public Map<String, Object> submitPromotion(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.submitPromotion(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/distribution/resSubmitPromotion.action")
	@ResponseBody
	public Map<String, Object> resSubmitPromotion(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.resSubmitPromotion(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/distribution/auditPromotion.action")
	@ResponseBody
	public Map<String, Object> auditPromotion(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.auditPromotion(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/distribution/resAuditPromotion.action")
	@ResponseBody
	public Map<String, Object> resAuditPromotion(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		promotionService.resAuditPromotion(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

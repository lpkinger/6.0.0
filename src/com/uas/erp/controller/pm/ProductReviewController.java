package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.ProductReviewService;

@Controller
public class ProductReviewController extends BaseController {
	@Autowired
	private ProductReviewService productReviewService;

	/**
	 * 保存form和grid数据
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            grid数据
	 */
	@RequestMapping("/pm/bom/saveProductReview.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.saveProductReview(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/pm/bom/deleteProductReview.action")
	@ResponseBody
	public Map<String, Object> deleteProductReview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.deleteProductReview(id, caller);
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
	@RequestMapping("/pm/bom/updateProductReview.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.updateProductReviewById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/pm/bom/submitProductReview.action")
	@ResponseBody
	public Map<String, Object> submitProductReview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.submitProductReview(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/pm/bom/resSubmitProductReview.action")
	@ResponseBody
	public Map<String, Object> resSubmitProductReview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.resSubmitProductReview(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/pm/bom/auditProductReview.action")
	@ResponseBody
	public Map<String, Object> auditProductReview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.auditProductReview(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/pm/bom/resAuditProductReview.action")
	@ResponseBody
	public Map<String, Object> resAuditProductReview(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.resAuditProductReview(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 更新产品需求参数
	 */
	@RequestMapping("/pm/bom/setNeedSpec.action")
	@ResponseBody 
	public Map<String, Object> setNeedSpec(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.setNeedSpec(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除产品需求参数
	 */
	@RequestMapping("/pm/bom/deleteNeedSpec.action")
	@ResponseBody 
	public Map<String, Object> deleteNeedSpec(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productReviewService.deleteNeedSpec(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

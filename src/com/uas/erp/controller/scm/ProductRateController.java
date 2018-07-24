package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.ProductRateService;

@Controller
public class ProductRateController extends BaseController {
	@Autowired
	private ProductRateService productRateService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveProductRate.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.saveProductRate(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteProductRate.action")  
	@ResponseBody 
	public Map<String, Object> deleteAgentPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.deleteProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateProductRate.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.updateProductRateById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printProductRate.action")  
	@ResponseBody 
	public Map<String, Object> printAgentPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.printProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitProductRate.action")  
	@ResponseBody 
	public Map<String, Object> submitProductRate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.submitProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitProductRate.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitProductRate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.resSubmitProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditProductRate.action")  
	@ResponseBody 
	public Map<String, Object> auditAgentPrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.auditProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditProductRate.action")  
	@ResponseBody 
	public Map<String, Object> resAuditProductRate(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		productRateService.resAuditProductRate(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

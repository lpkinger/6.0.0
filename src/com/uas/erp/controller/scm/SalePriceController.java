package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SalePriceService;

@Controller
public class SalePriceController extends BaseController {
	@Autowired
	private SalePriceService salePriceService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.saveSalePrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> deleteSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.deleteSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.updateSalePriceById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> printSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.printSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> submitSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.submitSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.resSubmitSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> auditSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.auditSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSalePrice(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.resAuditSalePrice(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 明细价格失效
	 */
	@RequestMapping("/scm/sale/abatesaleprice.action")  
	@ResponseBody 
	public Map<String, Object> abatesaleprice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.abatesalepricestatus(id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 明细价格有效
	 */
	@RequestMapping("/scm/sale/resabatesaleprice.action")  
	@ResponseBody 
	public Map<String, Object> resabatesaleprice(int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePriceService.resabatesalepricestatus(id);
		modelMap.put("success", true);
		return modelMap;
	}
	
}

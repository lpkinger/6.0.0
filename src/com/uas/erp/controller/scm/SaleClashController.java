package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleClashService;

@Controller
public class SaleClashController extends BaseController {
	@Autowired
	private SaleClashService saleClashService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.saveSaleClash(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.deleteSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.updateSaleClashById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> printSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.printSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> submitSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.submitSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.resSubmitSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> auditSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.auditSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleClash.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSaleClash(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleClashService.resAuditSaleClash(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

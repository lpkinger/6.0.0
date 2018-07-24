package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleChangeService;

@Controller
public class SaleChangeController extends BaseController {
	@Autowired
	private SaleChangeService saleChangeService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.saveSaleChange(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.deleteSaleChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.updateSaleChangeById(caller, formStore, param);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> submitSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.submitSaleChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.resSubmitSaleChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> auditSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.auditSaleChange( id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSaleChange(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleChangeService.resAuditSaleChange(caller, id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printSaleChange.action")  
	@ResponseBody 
	public Map<String, Object> print(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys = saleChangeService.printSaleChange(caller, id, reportName, condition);
		modelMap.put("success", true);
		modelMap.put("keyData",keys);
		return modelMap;
	}
}

package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleCloseService;

@Controller
public class SaleCloseController extends BaseController {
	@Autowired
	private SaleCloseService saleCloseService;
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteSaleClose.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleCloseService.deleteSaleClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitSaleClose.action")  
	@ResponseBody 
	public Map<String, Object> submitSaleClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleCloseService.submitSaleClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitSaleClose.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitSaleClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleCloseService.resSubmitSaleClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditSaleClose.action")  
	@ResponseBody 
	public Map<String, Object> auditSaleClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleCloseService.auditSaleClose( id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditSaleClose.action")  
	@ResponseBody 
	public Map<String, Object> resAuditSaleClose(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleCloseService.resAuditSaleClose(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

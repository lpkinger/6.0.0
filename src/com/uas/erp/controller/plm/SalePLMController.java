package com.uas.erp.controller.plm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.plm.SalePLMService;

@Controller
public class SalePLMController extends BaseController {
	@Autowired
	private SalePLMService salePLMService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/sale/saveSale.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.saveSale(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 */
	@RequestMapping("/plm/sale/deleteSale.action")  
	@ResponseBody 
	public Map<String, Object> delete(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.deleteSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/plm/sale/updateSale.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.updateSale(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/plm/sale/submitSale.action")  
	@ResponseBody 
	public Map<String, Object> submitPurchase(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.submitSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交试产销售单
	 */
	@RequestMapping("/plm/sale/resSubmitSale.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.resSubmitSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核试产销售单
	 */
	@RequestMapping("/plm/sale/auditSale.action")  
	@ResponseBody 
	public Map<String, Object> audit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.auditSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核试产销售单
	 */
	@RequestMapping("/plm/sale/resAuditSale.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.resAuditSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 打印试产销售单
	 */
	@RequestMapping("/plm/sale/printSale.action")  
	@ResponseBody 
	public Map<String, Object> print(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.printSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 结案
	 */
	@RequestMapping("/plm/sale/endSale.action")  
	@ResponseBody 
	public Map<String, Object> endSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.endSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反结案
	 */
	@RequestMapping("/plm/sale/resEndSale.action")  
	@ResponseBody 
	public Map<String, Object> resEndSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		salePLMService.resEndSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}

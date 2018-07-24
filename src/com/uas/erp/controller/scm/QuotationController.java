package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.b2b.model.QuotationDetailDet;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.QuotationService;

@Controller
public class QuotationController extends BaseController {
	@Autowired
	private QuotationService quotationService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveQuotation.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.saveQuotation(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteQuotation.action")  
	@ResponseBody 
	public Map<String, Object> deleteQuotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.deleteQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateQuotation.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.updateQuotationById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 打印
	 */
	@RequestMapping("/scm/sale/printQuotation.action")  
	@ResponseBody 
	public Map<String, Object> printQuotation(String caller, int id, String reportName, String condition) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String[] keys =quotationService.printQuotation(id, caller,reportName,condition);
		modelMap.put("success", true);
		modelMap.put("keyData", keys);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitQuotation.action")  
	@ResponseBody 
	public Map<String, Object> submitQuotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.submitQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitQuotation.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitQuotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.resSubmitQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditQuotation.action")  
	@ResponseBody 
	public Map<String, Object> auditQuotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.auditQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditQuotation.action")  
	@ResponseBody 
	public Map<String, Object> resAuditQuotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.resAuditQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 禁用
	 */
	@RequestMapping("/scm/sale/bannedQuotation.action")  
	@ResponseBody 
	public Map<String, Object> banned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.bannedQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反禁用
	 */
	@RequestMapping("/scm/sale/resBannedQuotation.action")  
	@ResponseBody 
	public Map<String, Object> resBanned(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.resBannedQuotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转销售
	 */
	@RequestMapping("/scm/sale/quoturnSale.action")  
	@ResponseBody 
	public Map<String, Object> turnSale(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int said = quotationService.turnSale(id, caller);
		modelMap.put("id", said);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 转价格库
	 */
	@RequestMapping("/scm/quotation/toSalePrice.action")  
	@ResponseBody 
	public Map<String, Object> toSalePrice(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int said = quotationService.toSalePrice(id, caller);
		modelMap.put("id", said);
		modelMap.put("success", true);
		return modelMap;
	}
	
	
	/**
	 * 取分段报价信息
	 * 
	 * @param in_id
	 *            询价单ID
	 */
	@RequestMapping("/b2b/sale/zdquotation/det.action")
	@ResponseBody
	public List<Map<String, Object>> getDet(Integer qu_id) {
		return quotationService.getStepDet(qu_id);
	}
	
	/**
	 * 查找报价信息
	 * 
	 * @param id
	 *            
	 */
	@RequestMapping("/b2b/sale/zdquotation/getReply.action")
	@ResponseBody
	public List<QuotationDetailDet> getReply(int id) {
		return quotationService.findReplyByInid(id);
	}
	
	@RequestMapping("/scm/sale/saveZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> saveZDquotation(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.saveZDquotation(formStore, param,param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/scm/sale/deleteZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> deleteZDquotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.deleteZDquotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/updateZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> updateZDquotation(String caller, String formStore, String param, String param2) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.updateZDquotation(formStore, param,param2, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/scm/sale/submitZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> submitZDquotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.submitZDquotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/scm/sale/resSubmitZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitZDquotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.resSubmitZDquotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/scm/sale/auditZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> auditZDquotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.auditZDquotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/scm/sale/resAuditZDquotation.action")  
	@ResponseBody 
	public Map<String, Object> resAuditZDquotation(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		quotationService.resAuditZDquotation(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}


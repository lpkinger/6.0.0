package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.scm.SaleProjectService;


@Controller
public class SaleProjectController extends BaseController {
	@Autowired
	private SaleProjectService saleProjectService;
	/**
	 * 保存PreSale
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/saveSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> save(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.saveSaleProject(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deleteSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> deleteSaleProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.deleteSaleProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/scm/sale/updateSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> update(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.updateSaleProjectById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核PreSale
	 */
	@RequestMapping("/scm/sale/auditSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.auditSaleProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核PreSale
	 */
	@RequestMapping("/scm/sale/resAuditSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.resAuditSaleProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交PreSale
	 */
	@RequestMapping("/scm/sale/submitSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> submit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.submitSaleProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交PreSale
	 */
	@RequestMapping("/scm/sale/resSubmitSaleProject.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		saleProjectService.resSubmitSaleProject(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转立项申请
	 */
	@RequestMapping("/scm/sale/turnProject.action")  
	@ResponseBody 
	public Map<String, Object> turnProject(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int prjid = saleProjectService.turnProject(id, caller);
		modelMap.put("id", prjid);
		modelMap.put("success", true);
		return modelMap;
	}
//	/**
//	 * 审核详情信息提取
//	 */
//	@RequestMapping("/scm/sale/getOtherPreSaleValues.action")  
//	@ResponseBody 
//	public Map<String, Object> getOtherPreSaleValues(String caller, int id) {
//		String language = (String)session.getAttribute("language");
//		Employee employee = (Employee)session.getAttribute("employee");
//		Map<String, Object> modelMap = new HashMap<String, Object>();
////		int said = preSaleService.turnSale(id, caller);
//		Map<String , Object> values = preSaleService.getOtherPreSaleValues(id);
//		modelMap.put("values", BaseUtil.parseMap2Str(values));
//		modelMap.put("success", true);
//		return modelMap;
//	}
//	/**
//	 * 转订单评审
//	 */
//	@RequestMapping("/scm/sale/turnPreSaleToSale.action")  
//	@ResponseBody 
//	public Map<String, Object> turnPreSaleToSale(String caller, int ps_id ,String type) {
//		String language = (String)session.getAttribute("language");
//		Employee employee = (Employee)session.getAttribute("employee");
//		Map<String, Object> modelMap = new HashMap<String, Object>();
//		String url = preSaleService.turnPreSaleToSale(caller,ps_id, type);
//		modelMap.put("clickurl", url);
//		modelMap.put("success", true);
//		return modelMap;
//	}
	
}

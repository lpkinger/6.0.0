package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.core.BaseUtil;
import com.uas.erp.service.scm.PreSaleService;


@Controller
public class PreSaleControlller extends BaseController {
	@Autowired
	private PreSaleService preSaleService;
	/**
	 * 保存PreSale
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/scm/sale/savePreSale.action")  
	@ResponseBody 
	public Map<String, Object> save(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.savePreSale(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 */
	@RequestMapping("/scm/sale/deletePreSale.action")  
	@ResponseBody 
	public Map<String, Object> deletePreSale(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.deletePreSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/scm/sale/updatePreSale.action")  
	@ResponseBody 
	public Map<String, Object> update(String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.updatePreSaleById(formStore, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核PreSale
	 */
	@RequestMapping("/scm/sale/auditPreSale.action")  
	@ResponseBody 
	public Map<String, Object> audit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.auditPreSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核PreSale
	 */
	@RequestMapping("/scm/sale/resAuditPreSale.action")  
	@ResponseBody 
	public Map<String, Object> resAudit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.resAuditPreSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交PreSale
	 */
	@RequestMapping("/scm/sale/submitPreSale.action")  
	@ResponseBody 
	public Map<String, Object> submit(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.submitPreSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交PreSale
	 */
	@RequestMapping("/scm/sale/resSubmitPreSale.action")  
	@ResponseBody 
	public Map<String, Object> resSubmit(int id,String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		preSaleService.resSubmitPreSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转销售
	 */
	@RequestMapping("/scm/sale/preSaleToSale.action")  
	@ResponseBody 
	public Map<String, Object> turnSale(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		int said = preSaleService.turnSale(id, caller);
		modelMap.put("id", said);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核详情信息提取
	 */
	@RequestMapping("/scm/sale/getOtherPreSaleValues.action")  
	@ResponseBody 
	public Map<String, Object> getOtherPreSaleValues(int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Map<String , Object> values = preSaleService.getOtherPreSaleValues(id);
		modelMap.put("values", BaseUtil.parseMap2Str(values));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转订单评审
	 */
	@RequestMapping("/scm/sale/turnPreSaleToSale.action")  
	@ResponseBody 
	public Map<String, Object> turnPreSaleToSale(int ps_id ,String type, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String url = preSaleService.turnPreSaleToSale(ps_id, type);
		modelMap.put("clickurl", url);
		modelMap.put("success", true);
		return modelMap;
	}
}

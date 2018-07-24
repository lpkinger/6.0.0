package com.uas.erp.controller.scm;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.service.scm.BomPriceService;

@Controller
public class BomPriceController {
	@Autowired
	private BomPriceService bomPriceService; 
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("scm/purchase/saveBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> saveBomPrice(String caller, String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.saveBomPrice(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("scm/purchase/deleteBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> deleteBomPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.deleteBomPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("scm/purchase/updateBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> updateBomPrice(String caller ,String formStore, String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.updateBomPrice(caller,formStore,param);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("scm/purchase/submitBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> submitBomPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.submitBomPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("scm/purchase/resSubmitBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitBomPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.resSubmitBomPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("scm/purchase/auditBomPricee.action")  
	@ResponseBody 
	public Map<String, Object> auditBomPricee(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.auditBomPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("scm/purchase/resAuditBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> resAuditBomPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.resAuditBomPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 材料成本计算
	 */
	@RequestMapping("scm/purchase/evlBomCostPrice.action")  
	@ResponseBody 
	public Map<String, Object> evlBomCostPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.evlBomCostPrice(caller,id);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 商城核价
	 */
	@RequestMapping("scm/purchase/b2cBomPrice.action")  
	@ResponseBody 
	public Map<String, Object> b2cBomPrice(String caller,int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		bomPriceService.b2cBomPrice(caller,id);
		return modelMap;
	}
	/**
	 * 转商城询价
	 */
	@RequestMapping("scm/purchase/turnB2cInquiry.action")  
	@ResponseBody 
	public Map<String, Object> turnB2cInquiry(String caller,int id,String gridId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("success", true);
		modelMap.put("log", bomPriceService.turnB2cInquiry(caller,id,gridId));
		return modelMap;
	}
}

package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MouldSaleService;

@Controller
public class MouldSaleController extends BaseController {
	@Autowired
	private MouldSaleService mouldSaleService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/saveMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.saveMouldSale(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/pm/mould/deleteMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> deleteMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.deleteMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/updateMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.updateMouldSaleById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> printMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.printMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> submitMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.submitMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.resSubmitMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> auditMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.auditMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditMouldSale.action")  
	@ResponseBody 
	public Map<String, Object> resAuditMouldSale(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.resAuditMouldSale(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 转模具出货单
	 */
	@RequestMapping("/pm/mould/turnDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> turnDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", mouldSaleService.turnDeliveryOrder(id, caller));
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 更新收款状态
	 */
	@RequestMapping("/pm/mould/mouldsale/updatechargestatus.action")  
	@ResponseBody 
	public Map<String, Object> updateChargeStatus(HttpSession session, int id, String returnstatus, String returnremark, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldSaleService.updateChargeStatus(id, returnstatus, returnremark, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}


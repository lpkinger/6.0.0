package com.uas.erp.controller.pm;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.uas.erp.core.BaseController;
import com.uas.erp.service.pm.MouldDeliveryOrderService;

@Controller
public class MouldDeliveryOrderController extends BaseController {
	@Autowired
	private MouldDeliveryOrderService mouldDeliveryOrderService;
	/**
	 * 保存form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/saveDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> save(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.saveMouldDeliveryOrder(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 删除
	 * 包括明细
	 */
	@RequestMapping("/pm/mould/deleteDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> deleteDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.deleteMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 修改form和grid数据
	 * @param formStore form数据
	 * @param param grid数据
	 */
	@RequestMapping("/pm/mould/updateDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> update(HttpSession session, String formStore, String param, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.updateMouldDeliveryOrderById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}/**
	 * 打印
	 */
	@RequestMapping("/pm/mould/printDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> printDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.printMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 提交
	 */
	@RequestMapping("/pm/mould/submitDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> submitDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.submitMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反提交
	 */
	@RequestMapping("/pm/mould/resSubmitDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> resSubmitDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.resSubmitMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 审核
	 */
	@RequestMapping("/pm/mould/auditDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> auditDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.auditMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反审核
	 */
	@RequestMapping("/pm/mould/resAuditDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> resAuditDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.resAuditMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 过账
	 */
	@RequestMapping("/pm/mould/postDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> postDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.postMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
	/**
	 * 反过账
	 */
	@RequestMapping("/pm/mould/resPostDeliveryOrder.action")  
	@ResponseBody 
	public Map<String, Object> resPostDeliveryOrder(HttpSession session, int id, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		mouldDeliveryOrderService.resPostMouldDeliveryOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}
}


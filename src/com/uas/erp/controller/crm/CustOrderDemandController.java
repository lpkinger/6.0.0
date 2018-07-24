package com.uas.erp.controller.crm;

import com.uas.opensys.service.OrderDemandService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class CustOrderDemandController {

	@Autowired
	private OrderDemandService orderDemandService;

	/**
	 * 保存
	 * 
	 * @param formStore
	 *            form数据
	 * @param param
	 *            其它数据
	 */
	@RequestMapping("/crm/aftersalemgr/saveOrderDemand.action")
	@ResponseBody
	public Map<String, Object> save(String caller, String formStore,
			String param) {System.out.println(1);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.saveOrderDemand(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 修改
	 */
	@RequestMapping("/crm/aftersalemgr/updateOrderDemand.action")
	@ResponseBody
	public Map<String, Object> update(String caller, String formStore,
			String param) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.updateOrderDemandById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/crm/aftersalemgr/deleteOrderDemand.action")
	@ResponseBody
	public Map<String, Object> delete(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.deleteOrderDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/crm/aftersalemgr/submitOrderDemand.action")
	@ResponseBody
	public Map<String, Object> submitOrderDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.submitOrderDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/crm/aftersalemgr/resSubmitOrderDemand.action")
	@ResponseBody
	public Map<String, Object> resSubmitOrderDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.resSubmitOrderDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/crm/aftersalemgr/auditOrderDemand.action")
	@ResponseBody
	public Map<String, Object> auditOrderDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.auditOrderDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/crm/aftersalemgr/resAuditOrderDemand.action")
	@ResponseBody
	public Map<String, Object> resAuditOrderDemand(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		orderDemandService.resAuditOrderDemand(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}

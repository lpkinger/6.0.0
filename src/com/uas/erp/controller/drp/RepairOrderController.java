package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RepairOrderController {

	@Autowired
	private RepairOrderService repairOrderService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveRepairOrder.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.saveRepairOrder(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateRepairOrder.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.updateRepairOrderById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteRepairOrder.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.deleteRepairOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitRepairOrder.action")
	@ResponseBody
	public Map<String, Object> submitRepairOrder(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.submitRepairOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitRepairOrder.action")
	@ResponseBody
	public Map<String, Object> resSubmitRepairOrder(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.resSubmitRepairOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditRepairOrder.action")
	@ResponseBody
	public Map<String, Object> auditRepairOrder(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.auditRepairOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditRepairOrder.action")
	@ResponseBody
	public Map<String, Object> resAuditRepairOrder(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairOrderService.resAuditRepairOrder(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	@RequestMapping("/drp/aftersale/turnRepairWork.action")
	@ResponseBody
	public Map<String, Object> turnRepairWork(int roid, String caller) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",repairOrderService.turnRepairWork(caller, roid));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 批量生成派工单
	 */
	@RequestMapping(value = "/drp/vastCreateRepairOrder.action")
	@ResponseBody
	public Map<String, Object> vastCreateRepairOrder(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",
				repairOrderService.batchCreateRepairOrder(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 派工单批量转维修单
	 */
	@RequestMapping(value = "/drp/vastTurnRepairWork.action")
	@ResponseBody
	public Map<String, Object> vastTurnRepairWork(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",
				repairOrderService.batchTurnRepairWork(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

}

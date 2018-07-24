package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.RepairWorkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RepairWorkController {

	@Autowired
	private RepairWorkService repairWorkService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveRepairWork.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.saveRepairWork(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateRepairWork.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.updateRepairWorkById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteRepairWork.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.deleteRepairWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitRepairWork.action")
	@ResponseBody
	public Map<String, Object> submitRepairWork(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.submitRepairWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitRepairWork.action")
	@ResponseBody
	public Map<String, Object> resSubmitRepairWork(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.resSubmitRepairWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditRepairWork.action")
	@ResponseBody
	public Map<String, Object> auditRepairWork(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.auditRepairWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditRepairWork.action")
	@ResponseBody
	public Map<String, Object> resAuditRepairWork(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairWorkService.resAuditRepairWork(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 维修单转报废单
	 */
	@RequestMapping(value = "/drp/batchTurnStockScrap.action")
	@ResponseBody
	public Map<String, Object> vastTurn(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", repairWorkService.batchTurnStockScrap(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 维修单转拨出单
	 */
	@RequestMapping(value = "/drp/batchTurnAppropriationOut.action")
	@ResponseBody
	public Map<String, Object> vastTurnRepairWork(String caller, String data) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log",
				repairWorkService.batchTurnAppropriationOut(data, caller));
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 费用转其它应收
	 */
	@RequestMapping(value = "/drp/TurnARBill.action")
	@ResponseBody
	public Map<String, Object> TurnARBill(String caller, int id) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		modelMap.put("log", repairWorkService.TurnARBill(caller, id));
		modelMap.put("success", true);
		return modelMap;
	}

}

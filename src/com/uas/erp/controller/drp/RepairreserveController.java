package com.uas.erp.controller.drp;

import com.uas.erp.service.drp.RepairreserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
public class RepairreserveController {

	@Autowired
	private RepairreserveService repairreserveService;

	/**
	 * 保存HrOrg
	 */
	@RequestMapping("/drp/aftersale/saveRepairreserve.action")
	@ResponseBody
	public Map<String, Object> save(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.saveRepairreserve(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 更改
	 */
	@RequestMapping("/drp/aftersale/updateRepairreserve.action")
	@ResponseBody
	public Map<String, Object> update(String formStore, String param,
			String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.updateRepairreserveById(formStore, param, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 删除
	 */
	@RequestMapping("/drp/aftersale/deleteRepairreserve.action")
	@ResponseBody
	public Map<String, Object> delete(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.deleteRepairreserve(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 提交
	 */
	@RequestMapping("/drp/aftersale/submitRepairreserve.action")
	@ResponseBody
	public Map<String, Object> submitRepairreserve(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.submitRepairreserve(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反提交
	 */
	@RequestMapping("/drp/aftersale/resSubmitRepairreserve.action")
	@ResponseBody
	public Map<String, Object> resSubmitRepairreserve(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.resSubmitRepairreserve(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 审核
	 */
	@RequestMapping("/drp/aftersale/auditRepairreserve.action")
	@ResponseBody
	public Map<String, Object> auditRepairreserve(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.auditRepairreserve(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

	/**
	 * 反审核
	 */
	@RequestMapping("/drp/aftersale/resAuditRepairreserve.action")
	@ResponseBody
	public Map<String, Object> resAuditRepairreserve(int id, String caller) {

		Map<String, Object> modelMap = new HashMap<String, Object>();
		repairreserveService.resAuditRepairreserve(id, caller);
		modelMap.put("success", true);
		return modelMap;
	}

}
